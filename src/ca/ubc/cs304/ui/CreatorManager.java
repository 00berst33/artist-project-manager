package ca.ubc.cs304.ui;

//import ca.ubc.cs304.delegates.LoginWindowDelegate;

import ca.ubc.cs304.delegates.CreatorManagerDelegate;
import ca.ubc.cs304.model.CreatorModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The class is only responsible for displaying and handling the login GUI.
 */
public class CreatorManager extends JFrame implements ActionListener {

    private static final String EXCEPTION_TAG = "[EXCEPTION]";
    private static final String WARNING_TAG = "[WARNING]";
    private static final int INVALID_INPUT = Integer.MIN_VALUE;
    private static final int EMPTY_INPUT = 0;



    private BufferedReader bufferedReader = null;

    private static final int TEXT_FIELD_WIDTH = 10;
//    private static final int MAX_LOGIN_ATTEMPTS = 3;

    // running accumulator for login attempts
    private int loginAttempts;

    // components of the login window
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField usernameField;
    private JLabel resultLabel;
    private JLabel signupLabel;
    private JButton loginButton;
    private JButton registerButton;

    // delegate
    private CreatorManagerDelegate delegate; //should be the unique delegate

    public CreatorManager() {
        super("Creator App User Login");

    }

    public void showLoginUI(CreatorManagerDelegate delegate) {
        this.delegate = delegate;

        // Frame setup
        JFrame frame = new JFrame("Creator Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);

        JPanel contentPane = new JPanel();
        this.setContentPane(contentPane);

        // Components
        JLabel emailLabel = new JLabel("Enter email: ");
        JLabel passwordLabel = new JLabel("Enter password: ");
        emailField = new JTextField(TEXT_FIELD_WIDTH);
        passwordField = new JPasswordField(TEXT_FIELD_WIDTH);
        passwordField.setEchoChar('*');

        JButton loginButton = new JButton("Log In");
//        JLabel resultLabel = new JLabel("");
        resultLabel = new JLabel(" ");
        resultLabel.setForeground(Color.RED);

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        contentPane.setLayout(gb);
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Email label
        c.fill = GridBagConstraints.NONE;  // label doesn't need to fill space
        c.weightx = 0;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.insets = new Insets(10, 10, 5, 0);
        gb.setConstraints(emailLabel, c);
        contentPane.add(emailLabel);

        // Email field
        c.fill = GridBagConstraints.HORIZONTAL;  // fill horizontally to show text box properly
        c.weightx = 1.0;                         // take available horizontal space
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(10, 0, 5, 10);
        gb.setConstraints(emailField, c);
        contentPane.add(emailField);

        // Password label
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.insets = new Insets(0, 10, 5, 0);
        gb.setConstraints(passwordLabel, c);
        contentPane.add(passwordLabel);

        // Password field
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(0, 0, 5, 10);
        gb.setConstraints(passwordField, c);
        contentPane.add(passwordField);

        // Login button
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(10, 10, 5, 10);
        gb.setConstraints(loginButton, c);
        contentPane.add(loginButton);

        // Result label
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(5, 10, 10, 10);

        gb.setConstraints(resultLabel, c);
        contentPane.add(resultLabel);

        loginButton.addActionListener(this);

        // anonymous inner class for closing the window
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // size the window to obtain a best fit for the components
        this.pack();

        // center the frame
        Dimension d = this.getToolkit().getScreenSize();
        Rectangle r = this.getBounds();
        this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

        // make the window visible
        this.setVisible(true);

        // place the cursor in the text field for the email
        emailField.requestFocus();
    }

    public void showSignupWindow(CreatorManagerDelegate delegate, String email, String password) {
        this.delegate = delegate;

        // Frame setup
        JFrame frame = new JFrame("Creator Signup");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);

        JPanel contentPane = new JPanel();
        this.setContentPane(contentPane);

        // Components
        JLabel usernameLabel = new JLabel("Pick a username: ");
        usernameField = new JTextField(TEXT_FIELD_WIDTH);
//        passwordField = new JPasswordField(TEXT_FIELD_WIDTH);
//        passwordField.setEchoChar('*');

        JButton registerButton = new JButton("Sign Up");
//        JLabel resultLabel = new JLabel("");
        signupLabel = new JLabel(" ");
        signupLabel.setForeground(Color.RED);

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        contentPane.setLayout(gb);
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // username label
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.insets = new Insets(10, 10, 5, 0);
        gb.setConstraints(usernameLabel, c);
        contentPane.add(usernameLabel);

        // username field
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(10, 0, 5, 10);
        gb.setConstraints(usernameField, c);
        contentPane.add(usernameField);

        // register button
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(10, 10, 5, 10);
        gb.setConstraints(registerButton, c);
        contentPane.add(registerButton);

        // label
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(5, 10, 10, 10);
        gb.setConstraints(signupLabel, c);
        contentPane.add(signupLabel);

        registerButton.addActionListener(this);

//        registerButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null, "Button 1 clicked!");
//            }
//        });

        // anonymous inner class for closing the window
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // size the window to obtain a best fit for the components
        this.pack();

        // center the frame
        Dimension d = this.getToolkit().getScreenSize();
        Rectangle r = this.getBounds();
        this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

        // make the window visible
        this.setVisible(true);

        // place the cursor in the text field for the email
        usernameField.requestFocus();
        signupLabel.setText("Username cannot be changed later on...");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        System.out.print(e.getSource());

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        String username;
        if (usernameField == null) {
             username = ""; //no username
        }
        else {
            username = usernameField.getText().trim();
        }


        if (email.isEmpty() || password.isEmpty()) {
            resultLabel.setText("Please fill in all fields.");
        }

        if (!delegate.usernameExists(username)) {
//            System.out.println("\n\nusername " + username + "\nemail: " + email + "\npassword: " + password);
            if(!username.isEmpty() ){
                CreatorModel model = new CreatorModel(email,
                        username,
                        password,
                        0);
                delegate.insertCreator(model);
                delegate.showCreators();
            }
            // insert new creator model
            // setup page
//                this.dispose();
//                delegate.loginSuccess();

        } else if (delegate.usernameExists(username)) {
            signupLabel.setText("Username already exists. Choose again.");
            usernameField.setText("");
        }


        if (!delegate.emailExists(email)) {
            resultLabel.setText("Email not found.");
//            this.dispose();
            showSignupWindow(delegate, email, password);
        } else if (!delegate.verifyLogin(email, password)) {
            resultLabel.setText("Incorrect password.");
            passwordField.setText(""); // clear password field
        } else {
            this.dispose();
            CreatorModel model = delegate.getCreator(email, password);
            delegate.loginSuccess(model);
        }
    }





    public void showFrame(CreatorManagerDelegate delegate) {
        this.delegate = delegate;

        JLabel emailLabel = new JLabel("Enter email: ");
        JLabel passwordLabel = new JLabel("Enter password: ");

        emailField = new JTextField(TEXT_FIELD_WIDTH);
        passwordField = new JPasswordField(TEXT_FIELD_WIDTH);
        passwordField.setEchoChar('*');

        JButton loginButton = new JButton("Log In");

        JPanel contentPane = new JPanel();
        this.setContentPane(contentPane);

        // layout components using the GridBag layout manager
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        contentPane.setLayout(gb);
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // place the username label
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.insets = new Insets(10, 10, 5, 0);
        gb.setConstraints(emailLabel, c);
        contentPane.add(emailLabel);

        // place the text field for the username
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(10, 0, 5, 10);
        gb.setConstraints(emailField, c);
        contentPane.add(emailField);

        // place password label
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.insets = new Insets(0, 10, 10, 0);
        gb.setConstraints(passwordLabel, c);
        contentPane.add(passwordLabel);

        // place the password field
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(0, 0, 10, 10);
        gb.setConstraints(passwordField, c);
        contentPane.add(passwordField);

        // place the login button
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(5, 10, 10, 10);
        c.anchor = GridBagConstraints.CENTER;
        gb.setConstraints(loginButton, c);
        contentPane.add(loginButton);

        // register login button with action event handler
        loginButton.addActionListener(this);

        // anonymous inner class for closing the window
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // size the window to obtain a best fit for the components
        this.pack();

        // center the frame
        Dimension d = this.getToolkit().getScreenSize();
        Rectangle r = this.getBounds();
        this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

        // make the window visible
        this.setVisible(true);

        // place the cursor in the text field for the email
        emailField.requestFocus();
    }

//    public void handleLoginFailed() {
//        loginAttempts++;
//        passwordField.setText(""); // clear password field
//    }

//    public boolean hasReachedMaxLoginAttempts() {
//        return (loginAttempts >= MAX_LOGIN_ATTEMPTS);
//    }

    public void setupDatabase(CreatorManagerDelegate delegate) {
        this.delegate = delegate;
        //delegate.databaseSetup();
        delegate.showCreators();
    }

//    public void setupDatabaseTerminal(CreatorManagerDelegate delegate) {
//        this.delegate = delegate;
//
//        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//        int choice = INVALID_INPUT;
//
//        while(choice != 1 && choice != 2) {
//            System.out.println("If you have a table called Creators in your database (capitialization of the name does not matter), it will be dropped and a new Creators table will be created.\nIf you want to proceed, enter 1; if you want to quit, enter 2.");
//
//            choice = readInteger(false);
//
//            if (choice != INVALID_INPUT) {
//                switch (choice) {
//                    case 1:
//                        delegate.databaseSetup();
//                        break;
//                    case 2:
//                        handleQuitOption();
//                        break;
//                    default:
//                        System.out.println(WARNING_TAG + " The number that you entered was not a valid option.\n");
//                        break;
//                }
//            }
//        }
//    }

    public void showMainMenu(CreatorManagerDelegate delegate) {
        this.delegate = delegate;

        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        int choice = INVALID_INPUT;

        while (choice != 5) {
            System.out.println();
            System.out.println("1. Insert creator");
//            System.out.println("2. Delete branch");
//            System.out.println("3. Update creator numDrafts");
            System.out.println("4. Show creator");
            System.out.println("5. Quit");
            System.out.print("Please choose one of the above 5 options: ");

            choice = readInteger(false);

            System.out.println(" ");

            if (choice != INVALID_INPUT) {
                switch (choice) {
                    case 1:
                        handleInsertOptionTerminal();
                        break;
//                    case 2:
//                        handleDeleteOption();
//                        break;
//                    case 3:
//                        handleUpdateOption();
//                        break;
                    case 4:
                        delegate.showCreators();
                        break;
                    case 5:
                        handleQuitOption();
                        break;
                    default:
                        System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
                        break;
                }
            }
        }

    }

    /**
     * ActionListener Methods
     */
//    @Override
//    public void actionPerformed(ActionEvent e) {
    ////        delegate.login(emailField.getText(), String.valueOf(passwordField.getPassword()));
//
//    }


//    @Override
    private void handleInsertOptionTerminal() {
        String email = null;
        while (email == null) {
            System.out.print("Please enter the email you wish to insert: ");
            email = readLine().trim();
        }

        String password = null;
        while (password == null || password.length() <= 0) {
            System.out.print("Please enter the password you wish to insert: ");
            password = readLine().trim();
        }

        String username = null;
        while (username == null || username.length() <= 0) {
            System.out.print("Please enter the username you wish to insert: ");
            username = readLine().trim();
        }

        CreatorModel model = new CreatorModel(email,
                username,
                password,
                0);
        delegate.insertCreator(model);
    }

    private int readInteger(boolean allowEmpty) {
        String line = null;
        int input = INVALID_INPUT;
        try {
            line = bufferedReader.readLine();
            input = Integer.parseInt(line);
        } catch (IOException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        } catch (NumberFormatException e) {
            if (allowEmpty && line.length() == 0) {
                input = EMPTY_INPUT;
            } else {
                System.out.println(WARNING_TAG + " Your input was not an integer");
            }
        }
        return input;
    }

    private String readLine() {
        String result = null;
        try {
            result = bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        return result;
    }

    private void handleQuitOption() {
        System.out.println("Good Bye!");

        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                System.out.println("IOException!");
            }
        }
        delegate.creatorManagerFinished();
    }


}
