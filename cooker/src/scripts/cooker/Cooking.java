package scripts.cooker;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import scripts.nodes.failsafe.LevelRequirementChecker;
import scripts.nodes.failsafe.VotingBoothCloser;
import scripts.nodes.save.SaveOnGameUpdate;
import scripts.api.beg.logging.Logger;
import scripts.task.Task;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static scripts.data.VariablesCooker.vars;

public class Cooking {

    private final List<scripts.framework.logic.task.Task> nodes;

    public Cooking() {
        this.nodes = new LinkedList<>();
        if (vars.saveOnSystemUpdate) {
            addNodes(new SaveOnGameUpdate());
        }
        addNodes(new VotingBoothCloser(), new NoRequiredLevelChecker(), new LevelRequirementChecker());
    }

    public void addNodes(scripts.framework.logic.task.Task... newNodes) {
        Collections.addAll(nodes, newNodes);
    }

    public void execute() {
        while (!this.taskComplete()) {
            for (scripts.framework.logic.task.Task node : nodes) {
                if (node.validate()) {
                    Mouse.setSpeed(General.random(vars.mouseSpeed - 7, vars.mouseSpeed + 11));
                    vars.painter.setStatus(node.toString());
                    if (!node.execute() && vars.printDebug) {
                        Logger.getLogger().info(node.getError());
                    }
                }
                General.sleep(10, 40);
            }
        }
    }

    private boolean taskComplete() {
        Task task = vars.tasks.get(0);
        int stopAtLevel = task.getStopLevel();
        if (stopAtLevel != 0 && Skills.getCurrentLevel(SKILLS.COOKING) >= stopAtLevel) {
            System.out.println("Level " + stopAtLevel + "was reached.");
            return true;
        }
        if (vars.painter.getAmountToCook() <= 0) {
            System.out.println("task " + task.getAmount() + " " + task.getProduct() + " is completed.");
            return true;
        }
        return false;
    }

}
