package com.salesforce.tests.fs.commands;

import com.salesforce.tests.fs.path.IsAFile;
import com.salesforce.tests.fs.path.IsNotFile;
import com.salesforce.tests.fs.path.Path;
import com.salesforce.tests.fs.path.PathCache;
import com.salesforce.tests.fs.utils.TabBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.IntStream;

public class CommandPrompt {

    private Path currentPath;
    private Map<String, CommandExecutor> promptValidCommands = new HashMap<>();
    private PathCache pathCache;

    private boolean isRunning = true;

    public CommandPrompt(Path currentPath) {
        this.currentPath = currentPath;
        this.pathCache = new PathCache();
        this.setValidCommands();
    }

    private void setValidCommands() {
        this.promptValidCommands.put("quit", quiteCommand());
        this.promptValidCommands.put("pwd", printCurrentDirectory());
        this.promptValidCommands.put("ls", printPathContents());
        this.promptValidCommands.put("mkdir", makeDirectory());
        this.promptValidCommands.put("cd", changeDirectory());
        this.promptValidCommands.put("touch", createFileCommand());
    }

    public void takeUserInput(){
        Scanner in = new Scanner(System.in);

        String commandInput = in.nextLine();
        String[] commandParamter = commandInput.length() > 1 ? commandInput.split(" ") : new String[]{};
        String commandName ;
        String commandArgument = null;

        if(commandParamter.length == 0) {
            System.out.println("Please enter a command");
            return;
        }
        else if(commandParamter.length == 1)
            commandName = commandParamter[0];

        else {
            commandName = commandParamter[0];
            commandArgument = commandParamter[1];
        }

        this.executeCommand(commandName, commandArgument);
    }

    private String[] getCommandParametersFrom(String [] commandParameters){
        String parameters = "";
        for(int i = 1; i < commandParameters.length; i++)
            parameters = StringUtils.join(parameters, " ", commandParameters[i]);
        return parameters.split(" ");
    }

    public void executeCommand(String commandName, Object param){
        if(this.isValidCommand(commandName))
            this.promptValidCommands.get(commandName).executeCommand(param);
    }

    private boolean isValidCommand(String commandName) {
        return this.promptValidCommands.get(commandName) != null;
    }

    public Path getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(Path currentPath) {
        this.currentPath = currentPath;
    }

    public Map<String, CommandExecutor> getPromptValidCommands() {
        return promptValidCommands;
    }

    public void setPromptValidCommands(Map<String, CommandExecutor> promptValidCommands) {
        this.promptValidCommands = promptValidCommands;
    }

    public PathCache getPathCache() {
        return pathCache;
    }

    public void setPathCache(PathCache pathCache) {
        this.pathCache = pathCache;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public CommandExecutor printCurrentDirectory(){
        return (commandParameter) -> System.out.println(this.currentPath.getAbsolutePathName());
    }

    public CommandExecutor printPathContents(){
        return (commandParameter) -> {
            String recursiveOption = (String) commandParameter;

            if(currentPath.canContainSubPaths()) {
                if("[-r]".equals(recursiveOption))
                    this.printPathContentsInPath(this.currentPath);

                else {
                    currentPath.getDirectoryContents()
                            .values()
                            .forEach(path -> System.out.println(path.getRelativePathName()));
                }
            }
        };
    }

    private void printPathContentsInPath(Path currentPath){
        if(currentPath.canContainSubPaths()){
            Collection<Path> currentPathSubPaths = currentPath.getDirectoryContents().values();
            currentPathSubPaths.forEach( subPath -> {
                String tabsPerPathLevel = this.getSpacesToTabFromCurrentPathLevel(subPath);
                String contentToPrint = tabsPerPathLevel;

                if(!subPath.canContainSubPaths()) {
                    contentToPrint += subPath.getRelativePathName();
                    System.out.println(contentToPrint);
                }
                else{
                    if(subPath.containsSubPaths()) {
                        contentToPrint += subPath.getAbsolutePathName();
                        System.out.println(contentToPrint);
                        printPathContentsInPath(subPath);
                    }
                    else {
                        contentToPrint += subPath.getRelativePathName();
                        System.out.println(contentToPrint);
                    }
                }
            });
        }
    }

    private String getSpacesToTabFromCurrentPathLevel(Path currentPath){
        IntStream rangeOfLevel = IntStream.range(0, currentPath.getCurrentPathLevel());
        TabBuilder tabBuilder = new TabBuilder();
        rangeOfLevel.forEach(level -> tabBuilder.addTabToString());
        return tabBuilder.getTabString();
    }

    public CommandExecutor makeDirectory(){
        return (methodParameters) -> {
            String pathName = (String) methodParameters;

            if(this.isInValidDirectoryName(pathName))
                System.out.println("Enter a directory name with no more than 100 characters and without any slashes");
            else {
                Path path = this.createPathFromCurrentWithName(pathName);
                if (!this.containsPath(path.getAbsolutePathName())) {
                    this.currentPath.addSubDirectory(path.getRelativePathName(), path);
                    this.pathCache.addPath(path.getAbsolutePathName(), path);
                } else
                    System.out.println("Directory already exists");
            }
        };
    }

    private boolean isInValidDirectoryName(String pathName){
        return StringUtils.isBlank(pathName) || StringUtils.length(pathName) > 100 || StringUtils.contains(pathName, "/");
    }

    private Path createPathFromCurrentWithName(String pathName){
        Path path = new Path(pathName, new IsNotFile());
        path.setParentPath(this.currentPath);
        path.setAbsolutePathNameFromParent();
        return path;
    }

    public List<String> getDirectoriesInPath(String pathName){
        return Arrays.asList(pathName.split("/"));
    }

    public CommandExecutor changeDirectory(){
        return (methodParameters) -> {
            String pathName = (String) methodParameters;

            if("..".equals(pathName) && (!this.currentPath.hasParent() || "/root".equals(this.currentPath.getRelativePathName())))
                return;

            else if("..".equals(pathName)  && this.currentPath.hasParent())
                this.currentPath = this.currentPath.getParentPath();

            else {
                if (this.currentPath.containsSubPath(pathName) ){
                    Path pathToChangeTo = this.currentPath.getSubPathByName(pathName);
                    if(pathToChangeTo.canContainSubPaths())
                        this.currentPath = this.currentPath.getSubPathByName(pathName);
                    else
                        System.out.println("cd can only be applied to Directories, not Files");

                }
                else
                    System.out.println("Directory not found");
            }
        };
    }




    public CommandExecutor createFileCommand(){
        return (methodParameters) -> {
            String fileNameToAdd = (String) methodParameters;
            if(this.isInValidDirectoryName(fileNameToAdd))
                System.out.println("Enter a directory name with  no more than 100 characters and 100 characters and without any slashes");

            else if(!this.currentPath.containsSubPath(fileNameToAdd)){
                Path path = new Path(fileNameToAdd, new IsAFile());
                path.setParentPath(this.currentPath);
                this.currentPath.addSubDirectory(fileNameToAdd, path);
            }
        };
    }

    public CommandExecutor quiteCommand(){
        return (commandParameter) -> {
            this.isRunning = false;
        };
    }

    public boolean containsPath(String pathName){
        return this.pathCache.containsPath(pathName);
    }

}
