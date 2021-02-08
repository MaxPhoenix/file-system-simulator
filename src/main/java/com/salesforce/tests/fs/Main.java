package com.salesforce.tests.fs;

import com.salesforce.tests.fs.commands.CommandPrompt;
import com.salesforce.tests.fs.path.IsNotFile;
import com.salesforce.tests.fs.path.Path;

/**
 * The entry point for the Test program
 */
public class Main {

    public static void main(String[] args) {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT */
        Path rootPath =  new Path("root", new IsNotFile());
        rootPath.setAbsolutePathNameFromParent();
        CommandPrompt commandPrompt = new CommandPrompt(rootPath);
        while(commandPrompt.isRunning()){
            commandPrompt.takeUserInput();
        }

    }


}
