package ca.ubc.cs304.controller;

import ca.ubc.cs304.database.*;
import ca.ubc.cs304.delegates.CreatorManagerDelegate;
import ca.ubc.cs304.delegates.GroupDelegate;
import ca.ubc.cs304.delegates.MainWindowDelegate;
import ca.ubc.cs304.model.BranchWithDrafts;
import ca.ubc.cs304.model.CreatorModel;
import ca.ubc.cs304.model.DraftWithComments;
import ca.ubc.cs304.model.Group;
import ca.ubc.cs304.model.*;

import ca.ubc.cs304.ui.CreatorManager;
import ca.ubc.cs304.ui.LoginWindow;
import ca.ubc.cs304.ui.MainWindow;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Controller
        implements CreatorManagerDelegate, MainWindowDelegate, GroupDelegate {
    // start initializes loginWindow and connection on successful login (initialzie
    // other field here too?)
    private Connection connection = null;

    // INSERT OTHER CONNECTION HANDLERS HERE
    private ConnectionHandler dbHandler = null;
    private BranchConnectionHandler bHandler = null;
    private CreatorConnectionHandler cHandler = null;
    private DraftConnectionHandler dftHandler = null;
    private GroupConnectionHandler gHandler = null;
    private ProjectsConnectionHandler pHandler = null;

    private LoginWindow loginWindow = null;
    private MainWindow mainWindow = null;

    private static final String ORACLE_USERNAME = "";
    private static final String ORACLE_PASSWORD = "";

    public Controller() {
        dbHandler = new ConnectionHandler();
    }

    private void start() {

//        loginWindow = new LoginWindow();
//        loginWindow.showFrame(this);

        connection = dbHandler.login(ORACLE_USERNAME, ORACLE_PASSWORD);

        if (connection != null) {

            // INSERT ADDITIONAL CLASSES IN DATABASE PACKAGE HERE
            this.connection = connection;
            bHandler = new BranchConnectionHandler(this.connection);
            cHandler = new CreatorConnectionHandler(this.connection);
            dftHandler = new DraftConnectionHandler(this.connection);

            gHandler = new GroupConnectionHandler(this.connection);

            pHandler = new ProjectsConnectionHandler(this.connection);


            // Launch creator login UI
            CreatorManager creatorManager = new CreatorManager();
            creatorManager.setupDatabase(this);
            creatorManager.showLoginUI(this);

        } else {
            System.err.println("Login failed. Check Oracle credentials.");
            System.exit(-1);
        }
    }

    // insert a creator with the given info
    public void insertCreator(CreatorModel model) {
        cHandler.insertCreator(model);
    }

    // shows information about all the creators
    public void showCreators() {
        CreatorModel[] models = cHandler.getCreatorInfo();

        for (int i = 0; i < models.length; i++) {
            CreatorModel model = models[i];

            // simplified output formatting; truncation may occur
            System.out.printf("%-40.40s", model.getEmail());
            System.out.printf("%-15.15s", model.getUsername());
            System.out.printf("%-15.15s", model.getPassword());
            System.out.printf("%-15.15s", model.getNumDrafts());
            System.out.println();
        }
    }

    // closes the creator
    public void creatorManagerFinished() {
        cHandler.close();
        cHandler = null;
        System.exit(0);
    }

    // checks if creator exists
    public boolean emailExists(String email) {
        return cHandler.lookupCreatorEmail(email);
    }

    // checks if creator input is successful
    public boolean verifyLogin(String email, String password) {
        return cHandler.verifyLogin(email, password);
    }

//    // activates for the signup page from creator handler if email doesnt exist
//    public void showSignupWindow(String email, String password) {
//
//    }
    public boolean usernameExists(String username) {
        return cHandler.lookupCreatorUsername(username);
    }

    // activates for the successful login from the creator handler
    public void loginSuccess(CreatorModel model) {
        mainWindow = new MainWindow(this, model);
        mainWindow.showHomeUI(); // maybe pass user info in here????
    }

    public CreatorModel getCreator(String username) {
        return cHandler.getCreator(username);
    }

    public CreatorModel getCreator(String email, String password) {
        return cHandler.getCreator(email, password);
    }

    // groups implementations
//    public void groupSetup() {
//        gHandler.groupSetup();
//    }

    public ArrayList<Group> getGroups() {
        return gHandler.getGroups();
    }
    public ArrayList<Group> filterGroups(String match, int minCreators) {
        return gHandler.filterGroups(match, minCreators);
    }
//    public Group[] filterGroups(String match) {
//        return gHandler.filterGroups(match);
//    }
    public int getNumCreators(int gID) {
        return gHandler.getNumCreators(gID);
    }

    // MAIN PAGE UI
    public void showGroupPage() {
        System.out.println("group");
    }

    public void showCreatorPage() {
        System.out.println("creator");
    }

    public void changePassword(String username, String password) {
        cHandler.changePassword(username, password);
//        showCreators();
    }

    public void changeEmail(String username, String email) {
        cHandler.changeEmail(username, email);
//        showCreators();
    }

    public void showProjectPage() {System.out.println("project");}

    public List<Project> getAllProjects() {
        return pHandler.getAllProjects();
    }

    public List<Work> getWorksInProject(int pID) {
        return pHandler.getWorksInProject(pID);
    }

    public List<Project> getProjectsByLastDraftDate(String lastDraftDate) {
        return pHandler.getProjectsByLastDraftDate(lastDraftDate);
    }

    public void logout() {System.out.println("logout");}

    // BRANCH / WORK PAGE
    public List<BranchWithDrafts> getBranchDataByWorkId(int workId) {
        return bHandler.getBranchesAndDraftsByWorkId(workId);
    }

    public int getCommentCountForDraft(int dID) {
        try {
            return dftHandler.getCommentCountForDraft(dID);
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to get comment count: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public List<String> getUsernamesForWork(int wId) {
        return dftHandler.getUsernamesForWork(wId);
    }

    @Override
    public List<Integer> getDraftsCommentedByAllUsers(List<String> selectedUsers) {
        return dftHandler.getDraftsCommentedByAllUsers(selectedUsers);
    }

    @Override
    public DraftWithComments getDraftWithCommentsAndReplies(int draftID) {
        return dftHandler.getDraftWithCommentsAndReplies(draftID);
    }

    public void deleteBranchById(int bID, int pID) {
        bHandler.deleteBranch(bID, pID);
    }

    public String getBranchName(int bID, int pID) {
        return bHandler.getBranchNameById(bID, pID);
    }

    /**
     * Main method called at launch time
     */
    public static void main(String args[]) {
        Controller controller = new Controller();
        controller.start();
    }
}
