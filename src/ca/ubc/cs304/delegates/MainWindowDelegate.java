package ca.ubc.cs304.delegates;

import ca.ubc.cs304.model.*;

import java.util.ArrayList;
import java.util.List;

public interface MainWindowDelegate {
    void showCreatorPage();
    void showGroupPage();
    void showProjectPage();
    void logout();

    void changeEmail(String username, String newEmail); //returns false if unable to change email (it already exists in creators)
    void changePassword(String username, String newPassword);

    List<BranchWithDrafts> getBranchDataByWorkId(int workId);
    void deleteBranchById(int bId, int pID);
    boolean emailExists(String email);
    int getCommentCountForDraft(int dID);
    List<String> getUsernamesForWork(int workId);
    List<Integer> getDraftsCommentedByAllUsers(List<String> selectedUsers);
    DraftWithComments getDraftWithCommentsAndReplies(int draftID);
    String getBranchName(int bID, int pID);

    public ArrayList<Group> getGroups();
    //    public Group filterGroups(String match);
    public ArrayList<Group> filterGroups(String match, int minCreators);
    public int getNumCreators(int gID);

    List<Project> getAllProjects();
    List<Work> getWorksInProject(int pID);
    List<Project> getProjectsByLastDraftDate(String lastDraftDate);
}
