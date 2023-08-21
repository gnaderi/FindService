package gnaderi.services;

import gnaderi.orgtree.Engagement;
import gnaderi.orgtree.Enterprise;
import gnaderi.orgtree.Organisation;
import gnaderi.orgtree.Person;

import java.util.*;
import java.util.stream.Collectors;

public class FindServiceImpl implements FindService {

    /**
     * Find a person node by name
     *
     * @param name the name of the person
     * @return a person with this name, or Empty if not present in the organisation
     */
    @Override
    public Optional<Person> findPersonByName(Enterprise enterprise, String name) {
        Queue<Organisation> queue = new LinkedList<>(enterprise.getOrganisations());
        while (!queue.isEmpty()) {
            Organisation currentOrg = queue.remove();
            if (currentOrg.getChildOrganisations() != null && !currentOrg.getChildOrganisations().isEmpty()) {
                queue.addAll(currentOrg.getChildOrganisations());
            }
            for (Engagement eng : currentOrg.getMembers()) {
                if (name.equalsIgnoreCase(eng.getPerson().getName())) {
                    return Optional.of(eng.getPerson());
                }
            }
        }
        return Optional.empty();
    }

    /**
     * List all organisations that person is directly engaged in
     *
     * @return a collection of organisations, which may be empty
     */
    @Override
    public Collection<Organisation> findOrganisationsEngagingPerson(Enterprise enterprise, Person person) {
        Queue<Organisation> queue = new LinkedList<>(enterprise.getOrganisations());
        Set<Organisation> directlyEngagedOrgs = new HashSet<>();
        while (!queue.isEmpty()) {
            Organisation currentOrg = queue.remove();
            if (currentOrg.getChildOrganisations() != null && !currentOrg.getChildOrganisations().isEmpty()) {
                queue.addAll(currentOrg.getChildOrganisations());
            }
            for (Engagement eng : currentOrg.getMembers()) {
                if (person.equals(eng.getPerson())) {
                    directlyEngagedOrgs.add(currentOrg);
                    break;
                }
            }
        }
        return directlyEngagedOrgs;
    }

    /**
     * List all people who have more than one engagement in this enterprise
     *
     * @return a collection of multiple-engaged people, which may be empty
     */
    @Override
    public Collection<Person> findPeopleWithMultipleEngagements(Enterprise enterprise) {
        Queue<Organisation> queue = new LinkedList<>(enterprise.getOrganisations());
        Map<Person, Integer> engagementMap = new HashMap<Person, Integer>();
        while (!queue.isEmpty()) {
            Organisation currentOrg = queue.remove();
            if (currentOrg.getChildOrganisations() != null && !currentOrg.getChildOrganisations().isEmpty()) {
                queue.addAll(currentOrg.getChildOrganisations());
            }
            for (Engagement eng : currentOrg.getMembers()) {
                if (engagementMap.containsKey(eng.getPerson())) {
                    engagementMap.put(eng.getPerson(), engagementMap.get(eng.getPerson()) + 1);
                } else {
                    engagementMap.put(eng.getPerson(), 1);
                }
            }
        }
        return engagementMap.entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toList());
    }
}
