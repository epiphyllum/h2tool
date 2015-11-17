```java
package com.epiphyllum.dt;

import com.epiphyllum.dt.api.representations.Contact;
import com.epiphyllum.dt.jdbi.dao.ContactDAO;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hary on 15/11/17.
 */
public class DaoTest {

    private H2Tool dbtool;

    @Before
    public void setup() {
        dbtool = new H2Tool("phonebook", "db/migration");
    }

    @Test
    public void testDao() {
        Contact c = new Contact("zhou", "chao", "98989999");
        ContactDAO dao = dbtool.create(ContactDAO.class);
        int id = dao.createContact(c);
        Contact g = dao.getContactById(id);
        c.setId(id);

        System.out.println("got g: " + g);

//        assertEquals(c,g);
        assertEquals(c.getFirstName(), g.getFirstName());
        assertEquals(c.getLastName(), g.getLastName());
        assertEquals(c.getPhone(), g.getPhone());
    }
}

```
