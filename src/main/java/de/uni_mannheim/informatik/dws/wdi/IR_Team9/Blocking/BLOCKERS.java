package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.Blocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.SortedNeighbourhoodBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class BLOCKERS {

    public static String blocker1Description;
    public static String blocker2Description;
    public static String blocker3Description;
    public static String blocker4Description;
    public static String blocker5Description;
    public static String blocker6Description;
    public static String blocker7Description;
    public static String blocker8Description;

    public static Blocker<Company, Attribute, Company, Attribute> getBlocker1(){
        blocker1Description = "Standard Blocker using 3-grams as well as starting letters as keys.";

        return new StandardRecordBlocker<Company, Attribute>(new CompanyQgramBlocking(3));
    }

    public static Blocker<Company, Attribute, Company, Attribute> getBlocker2(){
        blocker2Description = "Standard Blocker using 4-grams as well as starting letters as keys.";

        return new StandardRecordBlocker<Company, Attribute>(new CompanyQgramBlocking(4));
    }

    public static Blocker<Company, Attribute, Company, Attribute> getBlocker3(){
        blocker3Description = "Standard Blocker using starting letters as keys. These are preprocessed by removing frequent tokens, punctuations, whitespaces and are lowercased";

        return new StandardRecordBlocker<Company, Attribute>(new CompanyBlockingKeyByNameGenerator(true, true));
    }

    public static Blocker<Company, Attribute, Company, Attribute> getBlocker4(){
        blocker4Description = "Standard Blocker using starting letters as keys. These are preprocessed by removing punctuations, whitespaces and are lowercased";

        return new StandardRecordBlocker<Company, Attribute>(new CompanyBlockingKeyByNameGenerator(true, false));
    }

    public static Blocker<Company, Attribute, Company, Attribute> getBlocker5(){
        blocker5Description = "Sorted neigborhoodblocker with 3 gram key and w=20.";

        return new SortedNeighbourhoodBlocker<>(new CompanyQgramBlocking(3), 20);
    }

    public static Blocker<Company, Attribute, Company, Attribute> getBlocker6(){
        blocker6Description = "Sorted neigborhoodblocker with 3 gram key and w=40.";

        return new SortedNeighbourhoodBlocker<>(new CompanyQgramBlocking(3), 40);
    }

    public static Blocker<Company, Attribute, Company, Attribute> getBlocker7(){
        blocker7Description = "Sorted neigborhoodblocker with 3 gram key and w=60.";

        return new SortedNeighbourhoodBlocker<>(new CompanyQgramBlocking(3), 60);
    }

    public static Blocker<Company, Attribute, Company, Attribute> getBlocker8(){
        blocker8Description = "Sorted neigborhoodblocker with 3 gram key and w=60.";

        return new SortedNeighbourhoodBlocker<>(new CompanyBlockingKeyByNameGenerator(true, true), 60);
    }
}
