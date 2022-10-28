/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package appagenda;

import entidades.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Kristian Johansson Dougal
 */
public class AppAgenda {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        EntityManager em = emf.createEntityManager();
        
        
        
        // Iniciar una transacción
        em.getTransaction().begin();

        // Modificaciones
        Provincia provinciaCadiz = new Provincia();
        provinciaCadiz.setNombre("Cádiz");
        Provincia provinciaSevilla = new Provincia();
        provinciaSevilla.setNombre("Sevilla");
        
        
        /*
        // Cancelar todas las operaciones
        //em.getTransaction().rollback();
        
        // Imprimir todos los datos de la tabla Provincia
        Query queryProvincias = em.createNamedQuery("Provincia.findAll");
        List<Provincia> listProvincias = queryProvincias.getResultList();

        for(Provincia provincia : listProvincias){
            System.out.println(provincia.getNombre());
        }

        // Todas las provinicas con nombre "Cádiz"
        Query queryProvinciaCadiz = em.createNamedQuery("Provincia.findByNombre");
        queryProvinciaCadiz.setParameter("nombre", "Cádiz");
        List<Provincia> listProvinciasCadiz = queryProvinciaCadiz.getResultList();
        for(Provincia provinciaCadiz1:listProvinciasCadiz){
            System.out.println(provinciaCadiz1.getId()+":");
            System.out.println(provinciaCadiz1.getNombre());
        }
        
        // Asigna el código "CA" a aquellos objetos con nombre "Cádiz"
        for(Provincia provinciaCadiz1 : listProvinciasCadiz){
        provinciaCadiz1.setCodigo("CA");
        em.merge(provinciaCadiz1);
        }

        // Utilizando el método find buscamos las provincias por su índice
        Provincia provinciaId2=em.find(Provincia.class,2);
        if (provinciaId2 != null){
        System.out.print(provinciaId2.getId() + ":");
        System.out.println(provinciaId2.getNombre());
        } else {
        System.out.println("No hay ninguna provincia con ID=2");
        }

        // Eliminar Provincia cuyo ID sea 15
        Provincia provinciaId15 = em.find(Provincia.class, 15);
        if (provinciaId15 != null){
        em.remove(provinciaId15);
        }else{
        System.out.println("No hay ninguna provincia con ID=15");
        }*/
        
        // Volcado a la base de datos
        em.getTransaction().commit();
        
        // Persistencia de datos
        em.persist(provinciaCadiz);
        em.persist(provinciaSevilla);
               
        
        em.close();
        emf.close();
        try{
            DriverManager.getConnection("jdbc:derby:BDAgenda;shutdown=true");
        } catch (SQLException ex){
        }

    }
    
}
