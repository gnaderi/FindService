package gnaderi.services;

import gnaderi.orgtree.Engagement;
import gnaderi.orgtree.Enterprise;
import gnaderi.orgtree.Organisation;
import gnaderi.orgtree.Person;
import gnaderi.services.FindService;
import gnaderi.services.FindServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FindServiceTest {

    FindService findService;

    @BeforeEach
    public void setUp() {
        findService = new FindServiceImpl();
    }

    @Nested
    class FindServicesUniTests {

        @Nested
        class GivenAnEmptyEnterprise {
            Enterprise emptyEnterprise = new Enterprise(emptyList());

            @Test
            public void findsNoPeople() {
                assertTrue(findService.findPersonByName(emptyEnterprise, "any name").isEmpty());
            }
        }

        @Nested
        class GivenAPopulatedEnterprise {

            Person alice = new Person("Alice Arnold");
            Person bob = new Person("Bob Burns");
            Person james = new Person("James Jones");
            Person pierre = new Person("Pierre Martin");
            Person aliyah = new Person("Aliyah Williams");
            Person sue = new Person("Sue Smith");


            Enterprise sme = new Enterprise(
                    asList(
                            new Organisation("Executive",
                                    asList(
                                            new Engagement("CEO", alice),
                                            new Engagement("CTO", bob)
                                    ),
                                    asList(
                                            new Organisation("Sales",
                                                    asList(
                                                            new Engagement("Account Manager", james),
                                                            new Engagement("Sales Assistant", sue)
                                                    ),
                                                    emptyList()
                                            ),
                                            new Organisation("Engineering",
                                                    asList(
                                                            new Engagement("Software Engineer", aliyah),
                                                            new Engagement("Operations", pierre),
                                                            new Engagement("QA", sue)
                                                    ),
                                                    emptyList()
                                            )
                                    )
                            )
                    )
            );

            @Test
            public void exactMatchSuccess() {
                assertEquals(alice, findService.findPersonByName(sme, "Alice Arnold").get());
                assertEquals(sue, findService.findPersonByName(sme, "Sue Smith").get());
            }

            @Test
            public void caseInsensitiveMatchSuccess() {
                assertEquals(pierre, findService.findPersonByName(sme, "pierre martin").get());
            }

            @Test
            public void unknownMatchFailure() {
                assertTrue(findService.findPersonByName(sme, "Morticia Addams").isEmpty());
            }

            @Test
            public void partialMatchFailure() {
                assertTrue(findService.findPersonByName(sme, "alice").isEmpty());
            }

            @Test
            void findOrganisationsEngagingPerson() {
                assertEquals(2, findService.findOrganisationsEngagingPerson(sme, sue).size());
                assertEquals(1, findService.findOrganisationsEngagingPerson(sme, alice).size());
            }

            @Test
            void findPeopleWithMultipleEngagements() {
                assertEquals(1, findService.findPeopleWithMultipleEngagements(sme).size());
            }

        }

    }


}