package pl.itcity.cg.desktop.controller.common;

/**
 * Interface enabling execution of front layer actions
 *
 * @author Michal Adamczyk
 */
@FunctionalInterface
public interface AfterCloseExecutor {

    /**
     * executes front layer action
     */
    void execute();
}
