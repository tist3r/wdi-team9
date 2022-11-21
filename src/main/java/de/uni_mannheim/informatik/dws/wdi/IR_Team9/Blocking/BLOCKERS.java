package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.Blocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.SortedNeighbourhoodBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class BLOCKERS {

    public static int NUM_BLOCKERS = 14;

    public static String blocker1Description;
    public static String blocker2Description;
    public static String blocker3Description;
    public static String blocker4Description;
    public static String blocker5Description;
    public static String blocker6Description;
    public static String blocker7Description;
    public static String blocker8Description;


    /**
     * 
     * @param id id of the blocker
     * @return the blocker with the id
     * @throws IndexOutOfBoundsException when the specified id does not exist
     */
    public static Blocker<Company, Attribute, Company, Attribute> getBlockerByID(int id) throws IndexOutOfBoundsException{
        switch (id){
            case 1: return getBlocker1();
            case 2: return getBlocker2();
            case 3: return getBlocker3();
            case 4: return getBlocker4();
            case 5: return getBlocker5();
            case 6: return getBlocker6();
            case 7: return getBlocker7();
            case 8: return getBlocker8();
            case 9: return getBlocker9();
            case 10: return getBlocker10();
            case 11: return getBlocker11();
            case 12: return getBlocker12();

            case 13: return getBlocker13();
            case 14: return getBlocker14();

            default: throw new IndexOutOfBoundsException(String.format("Blocker with id %d does not exist, max is %d", id, NUM_BLOCKERS));
        }
    }

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

    public static Blocker<Company, Attribute, Company, Attribute> getBlocker9(){
        blocker8Description = "First letter of tokens after removing frequent tokens. Only blocker capable of handling the large dataset.";

        return new StandardRecordBlocker<>(new CompanyNameStartTokenGenerator(3, true));
    }

    public static Blocker<Company, Attribute, Company, Attribute> getBlocker10(){
        blocker8Description = "First letter of tokens after removing frequent tokens. Only blocker capable of handling the large dataset.";

        return new StandardRecordBlocker<>(new CompanyNameStartTokenGenerator(3, true, true));
    }

    public static Blocker<Company, Attribute, Company, Attribute> getBlocker11(){
        //blocker1Description = "Standard Blocker using 3-grams as well as starting letters as keys.";

        return new StandardRecordBlocker<Company, Attribute>(new CompanyQgramBlocking(3,false));
    }

    public static Blocker<Company, Attribute, Company, Attribute> getBlocker12(){
        //blocker1Description = "Standard Blocker using 3-grams as well as starting letters as keys.";

        return new StandardRecordBlocker<Company, Attribute>(new CompanyQgramBlocking(4,false));
    }

    public static Blocker<Company, Attribute, Company, Attribute> getBlocker13(){
        //blocker5Description = "Sorted neigborhoodblocker with 3 gram key and w=20.";

        return new SortedNeighbourhoodBlocker<>(new CompanyQgramBlocking(3, false), 20);
    }

    public static Blocker<Company, Attribute, Company, Attribute> getBlocker14(){
        //blocker5Description = "Sorted neigborhoodblocker with 3 gram key and w=20.";

        return new SortedNeighbourhoodBlocker<>(new CompanyQgramBlocking(3, false), 40);
    }
}
