package ca.ubc.cs304.delegates;

import ca.ubc.cs304.model.CreatorModel;

public interface CreatorManagerDelegate {
//    void creatorLogin(String email, String password);
    //public void databaseSetup();
    public void loginSuccess(CreatorModel
                             model);
//    public void deleteCreator(String email);
//    public void changePassword(String username, String newPassword);
//    public void changeEmail(String username, String newEmail);
    public void insertCreator(CreatorModel model);
    boolean emailExists(String email);
    boolean usernameExists(String username);
    boolean verifyLogin(String email, String password);
//    void showHomeScreen(); // homescreen to be shown once the login is successful
    public void showCreators();
//    public void updateCreator(String email, String username, String password, int numWorks);
    public void creatorManagerFinished();
//    public void showCreatorSetUp();
//    public void showSignupWindow(String email, String password);
    public CreatorModel getCreator(String username);
    public CreatorModel getCreator(String email, String password);
}
