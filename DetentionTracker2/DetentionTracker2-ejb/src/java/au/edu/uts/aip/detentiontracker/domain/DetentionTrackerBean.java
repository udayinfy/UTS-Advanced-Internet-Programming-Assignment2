/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uts.aip.detentiontracker.domain;

/**
 *
 * @author Alex
 */
import java.util.*;
import javax.ejb.*;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import au.edu.uts.aip.detentiontracker.domain.EncryptionUtility;
import javax.naming.NamingException;
import java.security.NoSuchAlgorithmException;

@Stateless
public class DetentionTrackerBean {
    
    @PersistenceContext
    private EntityManager em;
        

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Login> findAllPeople()
    {
        
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Login> query = builder.createQuery(Login.class);
        return em.createQuery(query).getResultList();
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Detention> findAllDetetions(String username)
    {
        Login managed = em.find(Login.class, username);
        return managed.getDetentions();
        
//        CriteriaBuilder builder = em.getCriteriaBuilder();
//        CriteriaQuery<Detention> query = builder.createQuery(Detention.class);
//        return em.createQuery(query).getResultList();
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Detention findDetention(int detentionID)
    {
        return em.find(Detention.class, detentionID);
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateDetention(Detention currentDetention)
    {
        em.merge(currentDetention);
        
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteDetention(int detentionID)
    {
        Detention detention = em.find(Detention.class, detentionID);
        em.remove(detention);
    }
    
    
    // this will probs be deleted.
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createDetention(Detention detention)
    {
        em.persist(detention);
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addLogin(Detention detention, Login login)
    {
        try{
            
            //detention has it's login set and created.
        detention.setLogin(login);
        createDetention(detention);
        
        // next we gonna add it to our login.
               
        // set the list of detentions 
        //also this is where we could restrict.
        login.getDetentions().add(detention);
        

 
        
      
        // NO FORGETTING THE CLOSE ALSO TRANSACTIONS
        //em.close();
        em.merge(login);
        
        }
        catch(EJBException e)
        {
            System.out.println(e);
            
        }
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteDetentionFromLogin(int detentionID, String username)
    {
        Detention detManaged = em.find(Detention.class, detentionID );
        Login managed = em.find(Login.class, username);
        
        managed.getDetentions().remove(detManaged);
        
        em.remove(detManaged);
    }
    
    // for the administrator page
    
    
    // change that name
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Login> allLoginsByPayingAccountType()
    {
         TypedQuery<Login> query;       
        query = em.createQuery("SELECT l FROM Login l WHERE l.accountType <> :free AND l.accountType <> :administrator AND l.token <> NULL ", Login.class );
         query.setParameter("free", AccountType.Free);
         query.setParameter("administrator", AccountType.Administrator);
         
        
        return query.getResultList();
    }
    
    
    //RENAME TO allLoginsThatAreNotAdmin
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Login> allLogins()
    {
         TypedQuery<Login> query;       
        query = em.createQuery("SELECT l FROM Login l WHERE l.accountType <> :administrator", Login.class );
         query.setParameter("administrator", AccountType.Administrator);
         
        
        return query.getResultList();
    }
    
    // LOGIN ONLY COMMANDS
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean userExists(String username)
    {
        TypedQuery<Login> query = em.createQuery("SELECT l FROM Login l WHERE l.username =:name", Login.class );
        query.setParameter("name", username);
        
        if(query.getResultList().size()==1)
        {
            return true;
        }
        return false;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createInitialLogin(Login login)  throws NoSuchAlgorithmException
    {
        try{
            // BIGGEST PLAYS AU
        login.setPassword( EncryptionUtility.hash256(login.getPassword()));
        
        em.persist(login);
        }
        catch(NoSuchAlgorithmException e)
        {
            
            System.out.println(e);
        }
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Login getLogin(String username)
    {
        Login loginToReturn = em.find(Login.class,username);
        
        return loginToReturn;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateLogin(Login login)
    {
        // DON'T FORGET THE RECEIPT
        List<Detention> managedDet = findAllDetetions(login.getUsername());
        List<Receipt> managedRec = findAllReceiptsForLogin(login.getUsername());
        login.setDetentions(managedDet);
        login.setReceipts(managedRec);
        em.merge(login);
    }
    
    
    // get all the logins
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
        public List<Receipt> findAllReceiptsForLogin(String username)
    {
        Login managed = em.find(Login.class, username);
        return managed.getReceipts();
        
    }
       @TransactionAttribute(TransactionAttributeType.REQUIRED) 
    public List<Receipt> findAllReceipts()
    {
         TypedQuery<Receipt> query;       
        query = em.createQuery("SELECT r FROM Receipt r ", Receipt.class );
        return query.getResultList();
        
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Object> findTotalCountOfStudentName(String username)
    {
        Query query;       
       // query = em.createQuery("SELECT d.FIRSTNAME, d.lastName, COUNT(*) as count FROM Detention d GROUP BY d.FIRSTNAME, d.LASTNAME ORDER BY count DESC", Detention.class );
        query = em.createQuery("SELECT COUNT(d.detentionID) as total, d.firstName, d.lastName FROM Detention d WHERE d.login.username = :user GROUP BY d.lastName, d.firstName ORDER BY total DESC", Detention.class );
        query.setParameter("user", username);
        List result = query.getResultList();
        
        //System.out.println(result);
        //for (Object entry : result) {
        //    System.out.println(Arrays.asList((Object[])entry));
        //}
        return result;
    }
        
        @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateReceipt(Receipt currentReceipt)
    {
        em.merge(currentReceipt);
        
    }
    
//    public Date findExpiryDateOfSubscription(String username)
//    {
//     
////        Receipt managed = em.find(Receipt.class, username);
////        return managed.getDateOfExpiry();
//        
//        TypedQuery<Receipt> query;       
//        query = em.createQuery("SELECT r.dateOfExpiry FROM Receipt r WHERE r.login.username =:name", Receipt.class );
//        query.setParameter("name", username);
//        if(query.getSingleResult().getDateOfExpiry() == null)
//        {
//            return null;
//        }
//        else
//        {
//        return query.getSingleResult().getDateOfExpiry();
//        }
//
//        
//        
//    }
    

    
    // this will probs be deleted.
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createReceipt(Receipt receipt)
    {
        em.persist(receipt);
    }
    
    public void addLoginToReceipt(Receipt receipt, Login login)
    {
        try{
            
        //detention has it's login set and created.
        receipt.setLogin(login);
        createReceipt(receipt);
        
        // next we gonna add it to our login.
               
        // set the list of detentions 
        //also this is where we could restrict.
        login.getReceipts().add(receipt);

      
        // NO FORGETTING THE CLOSE ALSO TRANSACTIONS
        //em.close();
        em.merge(login);
        
        }
        catch(EJBException e)
        {
            System.out.println(e);
            
        }
    }
}
