package ca.ubc.cs304.ui;

import ca.ubc.cs304.delegates.MainWindowDelegate;
import ca.ubc.cs304.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainWindow {
    private final MainWindowDelegate delegate;
    private CreatorModel loggedInUser;
    private static final int TEXT_FIELD_WIDTH = 10;

    private JFrame frame;
    private JPanel mainPanel;
    private JPanel sidebar;

    private JButton accountBtn;
    private JButton groupBtn;
    private JButton projectBtn;
    private JButton closeBtn;

    public MainWindow(MainWindowDelegate delegate) {
        this.delegate = delegate;
        this.loggedInUser = null;
    }

    public MainWindow(MainWindowDelegate delegate, CreatorModel model) {
        this.delegate = delegate;
        this.loggedInUser = model;
    }

    public void showHomeUI() {
        frame = createFrame();
        sidebar = createSidebar();
        mainPanel = createMainPanel();

        frame.add(sidebar, BorderLayout.WEST);
        frame.add(mainPanel, BorderLayout.CENTER);

        registerEventHandlers();

        delegate.showCreatorPage(); // default page

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ----------------------------
    // UI Component Creation
    // ----------------------------

    private JFrame createFrame() {
        JFrame f = new JFrame("Creator App");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(900, 600);
        f.setLayout(new BorderLayout());
        return f;
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(143, 220, 193), 0, getHeight(), new Color(255, 255, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(180, frame.getHeight()));

        // Title
        JLabel appLabel = new JLabel("Creator App");
        appLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        appLabel.setForeground(Color.WHITE);
        appLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Buttons
        accountBtn = createSidebarButton("My Account");
        groupBtn = createSidebarButton("Groups");
        projectBtn = createSidebarButton("Projects");
        closeBtn = createSidebarButton("Close");

        // Add to panel
        panel.add(appLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(accountBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(groupBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(projectBtn);

        panel.add(Box.createVerticalGlue());

        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(closeBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        return panel;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(143, 220, 193));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createMainPanel() {
        return new JPanel(new BorderLayout());
    }

    private void showBranchesUI(int wID) {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        JPanel branchesContainer = new JPanel();
        branchesContainer.setLayout(new BoxLayout(branchesContainer, BoxLayout.Y_AXIS));
        branchesContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        branchesContainer.setBackground(Color.WHITE);

        mainPanel.add(branchesContainer, BorderLayout.CENTER);

        // Sidebar for user filters
        JPanel userFilterPanel = new JPanel();
        userFilterPanel.setLayout(new BoxLayout(userFilterPanel, BoxLayout.Y_AXIS));
        userFilterPanel.setBorder(BorderFactory.createTitledBorder("Filter by Reviewers"));

        JScrollPane userScrollPane = new JScrollPane(userFilterPanel);
        userScrollPane.setPreferredSize(new Dimension(200, 0));
        mainPanel.add(userScrollPane, BorderLayout.EAST);

        java.util.List<String> selectedUsers = new ArrayList<>();

        mainPanel.revalidate();
        mainPanel.repaint();

        branchesContainer.removeAll();

        try {
            java.util.List<BranchWithDrafts> branchData = delegate.getBranchDataByWorkId(wID);

            Set<String> allUsers = new HashSet<>(delegate.getUsernamesForWork(wID));

            userFilterPanel.removeAll();
            selectedUsers.clear();

            for (String user : allUsers) {
                JCheckBox userCheckbox = new JCheckBox(user);
                userCheckbox.addActionListener(evt -> {
                    if (userCheckbox.isSelected()) {
                        selectedUsers.add(user);
                    } else {
                        selectedUsers.remove(user);
                    }

                    // Refetch based on selected users
                    java.util.List<Integer> filteredDrafts = delegate.getDraftsCommentedByAllUsers(selectedUsers);

                    // Rebuild branches view with highlighting or filtering
                    renderFilteredBranches(branchesContainer, branchData, filteredDrafts, wID);
                });
                userFilterPanel.add(userCheckbox);
            }

            userFilterPanel.revalidate();
            userFilterPanel.repaint();

            if (branchData.isEmpty()) {
                JLabel noResults = new JLabel("No branches found for Work ID: " + wID);
                noResults.setAlignmentX(Component.LEFT_ALIGNMENT);
                branchesContainer.add(noResults);
            }

            for (BranchWithDrafts bwd : branchData) {
                JPanel card = createBranchCard(bwd, wID);
                branchesContainer.add(card);
                branchesContainer.add(Box.createRigidArea(new Dimension(0, 15)));
            }

            branchesContainer.revalidate();
            branchesContainer.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void renderFilteredBranches(JPanel container, java.util.List<BranchWithDrafts> data, java.util.List<Integer> highlightDrafts, int wID) {
        container.removeAll();

        for (BranchWithDrafts bwd : data) {
            JPanel card = createBranchCard(bwd, wID);
            for (Component comp : card.getComponents()) {
                if (comp instanceof JButton button) {
                    String text = button.getText();
                    if (text.startsWith("Draft")) {
                        String[] parts = text.split(" ");
                        if (parts.length >= 2) {
                            try {
                                int draftId = Integer.parseInt(parts[1]);
                                if (highlightDrafts.contains(draftId)) {
                                    button.setBackground(new Color(255, 226, 104));
                                }
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }
            container.add(card);
            container.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        container.revalidate();
        container.repaint();
    }

    public void showDraftPage(int draftID, int wID) {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        DraftWithComments draftWithComments = delegate.getDraftWithCommentsAndReplies(draftID);
        if (draftWithComments == null) {
            JLabel errorLabel = new JLabel("Draft not found.");
            mainPanel.add(errorLabel, BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();
            return;
        }

        Draft draft = draftWithComments.getDraft();
        java.util.List<CommentWithReplies> commentList = draftWithComments.getCommentsWithReplies();
        String branchName = delegate.getBranchName(draft.getbID(), draft.getpID());

        // Draft Header Card
        JPanel headerCard = new RoundedPanel(15, new Color(230, 240, 250));
        headerCard.setLayout(new BoxLayout(headerCard, BoxLayout.Y_AXIS));
        headerCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel branchLabel = new JLabel("Branch: " + branchName);
        branchLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        branchLabel.setForeground(new Color(50, 90, 70));
        headerCard.add(branchLabel);

        JLabel draftInfo = new JLabel("Draft ID: " + draft.getdID() +
                " | Created by: " + draft.getUsername() +
                " | Created at: " + draft.getCreatedAt());
        draftInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        draftInfo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        headerCard.add(draftInfo);

        mainPanel.add(headerCard, BorderLayout.NORTH);

        // Comments & Replies
        JPanel commentContainer = new JPanel();
        commentContainer.setLayout(new BoxLayout(commentContainer, BoxLayout.Y_AXIS));
        commentContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        commentContainer.setBackground(Color.WHITE);

        if (commentList.isEmpty()) {
            JLabel noComments = new JLabel("No comments on this draft.");
            noComments.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            commentContainer.add(noComments);
        } else {
            for (CommentWithReplies commentWithReplies : commentList) {
                Comment comment = commentWithReplies.getComment();

                JPanel commentCard = new RoundedPanel(12, new Color(245, 245, 255));
                commentCard.setLayout(new BoxLayout(commentCard, BoxLayout.Y_AXIS));
                commentCard.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

                JLabel commentHeader = new JLabel("@" + comment.getUsername() + " | " + comment.getCreatedAt());
                commentHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
                commentCard.add(commentHeader);

                JLabel commentText = new JLabel("<html>" + comment.getText() + "</html>");
                commentText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                commentText.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                commentCard.add(commentText);

                // Replies
                for (Reply reply : commentWithReplies.getReplies()) {
                    JPanel replyCard = new RoundedPanel(10, new Color(255, 255, 240));
                    replyCard.setLayout(new BoxLayout(replyCard, BoxLayout.Y_AXIS));
                    replyCard.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
                    replyCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
                    replyCard.setAlignmentX(Component.LEFT_ALIGNMENT);

                    JLabel replyHeader = new JLabel("↳ @" + reply.getUsername() + " | " + reply.getCreatedAt());
                    replyHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    replyCard.add(replyHeader);

                    JLabel replyText = new JLabel("<html>" + reply.getText() + "</html>");
                    replyText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    replyText.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
                    replyCard.add(replyText);

                    replyCard.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(5, 20, 5, 0),
                            replyCard.getBorder()
                    ));

                    commentCard.add(replyCard);
                }

                commentCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(10, 0, 10, 0),
                        commentCard.getBorder()
                ));

                commentContainer.add(commentCard);
                commentContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(commentContainer);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Back Button
        JButton backButton = new JButton("Back to Branches");
        backButton.addActionListener(e -> showBranchesUI(wID));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(143, 220, 193));
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bottomPanel.add(backButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createBranchCard(BranchWithDrafts branch, int wId) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(206, 238, 227));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel branchLabel = new JLabel("[" + branch.getBranch().getbID() + "] : " + branch.getBranch().getBranchName());
        branchLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        branchLabel.setForeground(new Color(50, 90, 70));
        card.add(branchLabel);

        // Delete button
        JButton deleteButton = new JButton("Delete Branch");
        deleteButton.setBackground(new Color(246, 76, 76));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deleteButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        deleteButton.addActionListener(ev -> {
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Delete this branch and all its drafts?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                delegate.deleteBranchById(branch.getBranch().getbID(), branch.getBranch().getpID());

                JOptionPane.showMessageDialog(frame,
                        "Branch successfully deleted.",
                        "Delete Successful",
                        JOptionPane.INFORMATION_MESSAGE);

                showBranchesUI(wId);
            }
        });

        card.add(Box.createVerticalStrut(8));
        card.add(deleteButton);

        // Draft Buttons
        for (Draft draft : branch.getDrafts()) {
            int commentCount = delegate.getCommentCountForDraft(draft.getdID());
            JButton draftButton = new JButton("Draft " + draft.getdID() + " [" + commentCount + " comment" + (commentCount != 1 ? "s" : "") + "]");
            draftButton.setBackground(new Color(245, 255, 250));
            draftButton.setFocusPainted(false);
            draftButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            draftButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            draftButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            draftButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            draftButton.addActionListener(d -> showDraftPage(draft.getdID(), wId));
            card.add(Box.createVerticalStrut(8));
            card.add(draftButton);
        }

        return card;
    }

    private void showProjectPage() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        // Get list of projects from controller
        java.util.List<Project> projects = delegate.getAllProjects();

        if (projects == null || projects.isEmpty()) {
            JLabel errorLabel = new JLabel("No projects.");
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(errorLabel, BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();
            return;
        }

        // Header Card
        JPanel headerCard = new RoundedPanel(15, new Color(230, 240, 250));
        headerCard.setLayout(new BoxLayout(headerCard, BoxLayout.Y_AXIS));
        headerCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel projectLabel = new JLabel("Project List");
        projectLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        projectLabel.setForeground(new Color(50, 90, 70));
        headerCard.add(projectLabel);

        // Panel to filter projects by date most recently updated
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Updated after (yyyy-mm-dd):");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        JFormattedTextField txtDate = new JFormattedTextField(df);
        txtDate.setColumns(10);
        JButton fetchButton = new JButton("Filter projects");

        inputPanel.add(label);
        inputPanel.add(txtDate);
        inputPanel.add(fetchButton);

        headerCard.add(inputPanel);
        mainPanel.add(headerCard, BorderLayout.NORTH);

        // Container for project cards
        JPanel projectListPanel = new JPanel();
        projectListPanel.setLayout(new BoxLayout(projectListPanel, BoxLayout.Y_AXIS));
        projectListPanel.setBackground(Color.WHITE);

        for (Project project : projects) {
            JPanel card = createProjectCard(project);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
            projectListPanel.add(Box.createVerticalStrut(10));
            projectListPanel.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(projectListPanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();


        fetchButton.addActionListener(e -> {
            //txt.removeAll();

            String date = txtDate.getText().trim();

            if (date.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a date.");
                return;
            }

            try {
                java.util.List<Project> projectFilter = delegate.getProjectsByLastDraftDate(date);

                projectListPanel.removeAll();

//                // Container for project cards
//                JPanel projectListPanel = new JPanel();
//                projectListPanel.setLayout(new BoxLayout(projectListPanel, BoxLayout.Y_AXIS));
//                projectListPanel.setBackground(Color.WHITE);

                if (projectFilter.isEmpty()) {
                    JLabel noResults = new JLabel("No projects found for date: " + date);
                    noResults.setAlignmentX(Component.LEFT_ALIGNMENT);
                    projectListPanel.add(noResults);
                }

                for (Project project : projectFilter) {
                    JPanel card = createProjectCard(project);
                    card.setAlignmentX(Component.LEFT_ALIGNMENT);
                    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
                    projectListPanel.add(Box.createVerticalStrut(10));
                    projectListPanel.add(card);
                }

                projectListPanel.revalidate();
                projectListPanel.repaint();

            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    private void showGroupPage() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        ArrayList<Group> groups = delegate.getGroups();

        // Top input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel match = new JLabel("Must contain these words: ");
        JTextField matchField = new JTextField(10);
        JLabel minCreatorsLabel = new JLabel("Minimum # of Creators: ");
        JTextField minCreatorsField = new JTextField(10);
        JButton fetchButton = new JButton("Fetch");

        inputPanel.add(match);
        inputPanel.add(matchField);
        inputPanel.add(minCreatorsLabel);
        inputPanel.add(minCreatorsField);
        inputPanel.add(fetchButton);

        if (groups == null || groups.isEmpty()) {
            JLabel errorLabel = new JLabel("No groups.");
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(errorLabel, BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();
            return;
        }

        // Header Card
//        JPanel headerCard = new RoundedPanel(15, new Color(230, 240, 250));
//        headerCard.setLayout(new BoxLayout(headerCard, BoxLayout.Y_AXIS));
//        headerCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
//
//        JLabel projectLabel = new JLabel("Group List");
//        projectLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
//        projectLabel.setForeground(new Color(50, 90, 70));
//        headerCard.add(projectLabel);
//
//        mainPanel.add(headerCard, BorderLayout.NORTH);

        // Container for group cards
        JPanel groupListPanel = new JPanel();
        groupListPanel.setLayout(new BoxLayout(groupListPanel, BoxLayout.Y_AXIS));
        groupListPanel.setBackground(Color.WHITE);

        for (Group group : groups) {
            JPanel card = createGroupCard(group);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
            groupListPanel.add(Box.createVerticalStrut(10));
            groupListPanel.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(groupListPanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        mainPanel.revalidate();
        mainPanel.repaint();

        fetchButton.addActionListener(e -> {
            mainPanel.removeAll();
            mainPanel.setLayout(new BorderLayout());

            groupListPanel.removeAll();

            try {


                String matchText = matchField.getText().trim();
                String minCreatorText = minCreatorsField.getText().trim();
                ArrayList<Group> groupsFiltered;

                if (minCreatorText.isEmpty()) {
                    groupsFiltered = delegate.filterGroups(matchText, 0);
                } else if (matchText.isEmpty()) {
                    int minCreator = Integer.parseInt(minCreatorText);
                    groupsFiltered = delegate.filterGroups("", minCreator);
                } else {
                    int minCreator = Integer.parseInt(minCreatorText);
                    groupsFiltered = delegate.filterGroups(matchText, minCreator);
                }

                for (Group group : groupsFiltered) {
                    JPanel card = createGroupCard(group);
                    card.setAlignmentX(Component.LEFT_ALIGNMENT);
                    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
                    groupListPanel.add(Box.createVerticalStrut(10));
                    groupListPanel.add(card);
                }

//                JScrollPane scrollPane = new JScrollPane(groupListPanel);
                scrollPane.setBorder(null);
                mainPanel.add(scrollPane, BorderLayout.CENTER);
                mainPanel.add(inputPanel, BorderLayout.NORTH);

                mainPanel.revalidate();
                mainPanel.repaint();
//                groupListPanel.revalidate();
//                groupListPanel.repaint();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid number of creators (must be a positive integer).");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                 ex.printStackTrace();
            }

        });

    }

    private void showCreatorPage() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        // Header Card
        JPanel headerCard = new RoundedPanel(15, new Color(230, 240, 250));
        headerCard.setLayout(new BoxLayout(headerCard, BoxLayout.Y_AXIS));
        headerCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        String username = this.loggedInUser.getUsername();

        JLabel usernameLabel = new JLabel(username + " works: " + loggedInUser.getNumDrafts());
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        usernameLabel.setForeground(new Color(50, 90, 70));
        headerCard.add(usernameLabel);

        // update label
        JLabel infoLabel = new JLabel(" ");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoLabel.setForeground(new Color(50, 90, 70));
        mainPanel.add(infoLabel, BorderLayout.CENTER);

        // change email
        JPanel inputPanelR = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField emailInput = new JTextField(TEXT_FIELD_WIDTH);
        inputPanelR.add(emailInput);
        JButton changeEmailButton = new JButton("Change Email");
        changeEmailButton.addActionListener(e -> {
            if (emailInput.getText().trim().isEmpty()) {
                infoLabel.setText("Email field must not be empty.");
            } else if (delegate.emailExists(emailInput.getText())) {
                emailInput.setText("");
                infoLabel.setText("Email is already in use.");

            } else {
                delegate.changeEmail(username,emailInput.getText());
                infoLabel.setText("Email changed!");
            }

        });
        inputPanelR.add(changeEmailButton, BorderLayout.EAST);

        //change password
        JTextField passwordInput = new JTextField(TEXT_FIELD_WIDTH);
        inputPanelR.add(passwordInput);
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> {
            if (passwordInput.getText().trim().isEmpty()) {
                infoLabel.setText("Password field must not be empty.");
            } else {
                String password = passwordInput.getText().trim();
                delegate.changePassword(username, password);
            }
        });
        inputPanelR.add(changePasswordButton, BorderLayout.SOUTH);

        mainPanel.add(inputPanelR, BorderLayout.SOUTH);

        mainPanel.add(headerCard, BorderLayout.NORTH);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createGroupCard(Group group) {
        JPanel card = new RoundedPanel(15, new Color(245, 250, 255));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel nameLabel = new JLabel("Project Name: " + group.getName() + " [gID#"+group.getgID()+"]");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(new Color(40, 70, 100));
        card.add(nameLabel);

        JLabel descLabel = new JLabel("Statement: " + group.getStatement());
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(descLabel);

        JLabel gidLabel = new JLabel("Number of Creators: " + delegate.getNumCreators(group.getgID()));
        gidLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        gidLabel.setForeground(new Color(100, 100, 120));
        card.add(gidLabel);

        return card;
    }

//    private void checkEmailUpdate(String email)


    private JPanel createProjectCard(Project project) {
        JPanel card = new RoundedPanel(15, new Color(245, 250, 255));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        String pTitle = project.getTitle();
        JButton nameButton = new JButton("Projects Name: " + pTitle);
        nameButton.addActionListener(e -> showWorkPage(project.getpID(), pTitle));
        nameButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        nameButton.setForeground(Color.WHITE);
        nameButton.setBackground(new Color(106, 11, 83));
        nameButton.setFocusPainted(false);
        nameButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        nameButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.add(nameButton);

        JLabel descLabel = new JLabel("Description: " + project.getDescription());
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(descLabel);

        JLabel gidLabel = new JLabel("GID: " + project.getgID());
        gidLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        gidLabel.setForeground(new Color(100, 100, 120));
        card.add(gidLabel);

        return card;
    }

    public void showWorkPage(int pID, String title) {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        //System.out.println("work");

        // Get list of works from controller
        java.util.List<Work> works = delegate.getWorksInProject(pID);

        if (works == null || works.isEmpty()) {
            JLabel errorLabel = new JLabel("No works.");
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(errorLabel, BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();
            return;
        }

        // Header Card
        JPanel headerCard = new RoundedPanel(15, new Color(230, 240, 250));
        headerCard.setLayout(new BoxLayout(headerCard, BoxLayout.Y_AXIS));
        headerCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel workLabel = new JLabel("Works in " + title);
        workLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        workLabel.setForeground(new Color(50, 90, 70));
        headerCard.add(workLabel);

        mainPanel.add(headerCard, BorderLayout.NORTH);

        // Container for work cards
        JPanel workListPanel = new JPanel();
        workListPanel.setLayout(new BoxLayout(workListPanel, BoxLayout.Y_AXIS));
        workListPanel.setBackground(Color.WHITE);

        for (Work work : works) {
            JPanel card = createWorkCard(work);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
            workListPanel.add(Box.createVerticalStrut(10));
            workListPanel.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(workListPanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createWorkCard(Work work) {
        System.out.println("card");
        JPanel card = new RoundedPanel(15, new Color(245, 250, 255));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JButton nameButton = new JButton("Work Name: " + work.getWorkName());
        nameButton.addActionListener(e -> showBranchesUI(work.getwID()));
        nameButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameButton.setForeground(Color.WHITE);
        nameButton.setBackground(new Color(106, 11, 83));
        nameButton.setFocusPainted(false);
        nameButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        nameButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.add(nameButton);

//        JLabel descLabel = new JLabel("File type: " + work.getClass());
//        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        card.add(descLabel);

        JLabel dateLabel = new JLabel("Created on: " + work.getCreatedAt());
        dateLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        dateLabel.setForeground(new Color(100, 100, 120));
        card.add(dateLabel);

        JLabel widLabel = new JLabel("WID: " + work.getwID());
        widLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        widLabel.setForeground(new Color(100, 100, 120));
        card.add(widLabel);

        return card;
    }


    // ----------------------------
    // Event Handling
    // ----------------------------

    private void registerEventHandlers() {
        accountBtn.addActionListener(e -> showCreatorPage());
        groupBtn.addActionListener(e -> showGroupPage());
        projectBtn.addActionListener(e -> showProjectPage());
        closeBtn.addActionListener(e -> System.exit(0));
    }
}
