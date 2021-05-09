package org.act.rscat.cat;

import java.io.IOException;

import org.act.rscat.sol.InfeasibleTestConfigException;

/**
 * This interface defines a CAT engine that assigns items/passages to an
 * examinee by adapting to the estimated ability at different testing stages.
 */
public interface CatEngine {

    /**
     * Defines the headers used in M
     */
    enum MapIndiceHeader {

        /**
         * Item identifiers
         */
        ITEM_IDENTIFIERS,

        /**
         * Passage identifiers
         */
        PASSAGE_IDENTIFIERS,

        /**
         * Item row indices in the item table
         */
        ITEM_INDICES,

        /**
         * Item Fisher information
         */
        FISHER_INFORMATION,

        /**
         * Item administration (boolean) status
         */
        ITEMS_ADMINISTERED,

        /**
         * Item order in passage
         */
        PASSAGE_ITEM_ORDER;
    }

    /**
     * Runs a CAT engine cycle based on the CAT input. For the fully adaptive
     * testing, it runs a cycle for a stage.
     *
     * @param catInput the instance of {@link CatInput}
     * @return the instance of {@link CatOutput} including the list of items to
     *         administer, number of items to administer, current theta estimate,
     *         and test status.
     * @throws IOException                   if there is an exception.
     * @throws InfeasibleTestConfigException if the test configuration is infeasible
     */
    CatOutput runsCatCycle(CatInput catInput) throws IOException, InfeasibleTestConfigException;
}
