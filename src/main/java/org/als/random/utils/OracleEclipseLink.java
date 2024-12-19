package org.als.random.utils;

/*
import org.als.random.domain.Person;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;
*/

public class OracleEclipseLink {


    /*public EntityManager getEntityManager(){
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPersistenceUnit");
            // Obtener el EntityManager
            return emf.createEntityManager();
    }

    public void testQuery() {
        // ... (definición de la entidad ProductoNvarchar con un campo codigoNvarchar)

        String nativeQuery = "SELECT CAST(p.name AS NVARCHAR2) AS codigo_nvarchar FROM Person p";
        Query query = getEntityManager().createNativeQuery(nativeQuery, Person.class);

        List<Person> resultados = query.getResultList();
        System.out.println(resultados);
    }

    public static void main( String[] args ){
        OracleEclipseLink oracleEclipseLink = new OracleEclipseLink();
        oracleEclipseLink.testQuery();
    }
     */
}
