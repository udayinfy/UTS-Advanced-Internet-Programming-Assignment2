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
        
    public void addSampleData(){
        
    }
    
    public List<Login> findAllPeople()
    {
        
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Login> query = builder.createQuery(Login.class);
        return em.createQuery(query).getResultList();
    }
    
    public List<Detention> findAllDetetions(String username)
    {
        Login managed = em.find(Login.class, username);
        return managed.getDetentions();
        
//        CriteriaBuilder builder = em.getCriteriaBuilder();
//        CriteriaQuery<Detention> query = builder.createQuery(Detention.class);
//        return em.createQuery(query).getResultList();
    }
    
    public Detention findDetention(int detentionID)
    {
        return em.find(Detention.class, detentionID);
    }
    
    public void updateDetention(Detention currentDetention)
    {
        em.merge(currentDetention);
        
    }
    
    public void deleteDetention(int detentionID)
    {
        Detention detention = em.find(Detention.class, detentionID);
        em.remove(detention);
    }
    
    
    // this will probs be deleted.
    public void createDetention(Detention detention)
    {
        em.persist(detention);
    }
    
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
    public void deleteDetentionFromLogin(int detentionID, String username)
    {
        Detention detManaged = em.find(Detention.class, detentionID );
        Login managed = em.find(Login.class, username);
        
        managed.getDetentions().remove(detManaged);
        
        em.remove(detManaged);
    }
    
    // for the administrator page
    
    
    // change that name
    public List<Login> allthelogins()
    {
         TypedQuery<Login> query;
         
         // fuck enums why can't you just string for me
         
        query = em.createQuery("SELECT l FROM Login l   ", Login.class );
         return query.getResultList();
    }
    
    
    // LOGIN ONLY COMMANDS
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
    
    public Login getLogin(String username)
    {
        Login loginToReturn = em.find(Login.class,username);
        
        return loginToReturn;
    }
    public void updateLogin(Login login)
    {
        // DON'T FORGET THE RECEIPT
        List<Detention> managed = findAllDetetions(login.getUsername());
        login.setDetentions(managed);
        em.merge(login);
    }
}
