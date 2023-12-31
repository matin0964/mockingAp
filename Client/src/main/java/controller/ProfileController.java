package controller;

import com.google.gson.Gson;
import model.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class ProfileController {

    private final User currentUser;
    private static final int PORT = 8100;

    public ProfileController(User currentUser) {
        this.currentUser = currentUser;
    }

    public static void addFromFriendList(User user, User currentUser) {
        try {
            Socket socket = new Socket("localhost", PORT);
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            String[] json = {"addFromFriendList", user.getUsername(), currentUser.getUsername()};
            writer.writeUTF(new Gson().toJson(json));
            writer.flush();
            //todo read and change yourself
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeFromFriendList(User user, User currentUser) {
        try {
            Socket socket = new Socket("localhost", PORT);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            String[] json = {"removeFromFriendList", user.getUsername(), currentUser.getUsername()};
            dos.writeUTF(new Gson().toJson(json));
            dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList rankPlayers() {
        try {
            Socket socket = new Socket("localhost", PORT);
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            DataInputStream reader = new DataInputStream(socket.getInputStream());
            String[] json = {"rankPlayers"};
            writer.writeUTF(new Gson().toJson(json));
            writer.flush();
            ArrayList<User> toReturn = new ArrayList<>();
            while (true) {
                String input = reader.readUTF();
                if (input.equals("finished")) {
                    return toReturn;
                }
                toReturn.add(new Gson().fromJson(input, User.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void sendFriendRequest(User user, User currentUser) {
        try {
            Socket socket = new Socket("localhost", PORT);
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            String[] json = {"sendFriendRequest", user.getUsername(), currentUser.getUsername()};
            writer.writeUTF(new Gson().toJson(json));
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setUserOnline(User currentUser) {
        try {
            Socket socket = new Socket("localhost", PORT);
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            String[] json = {"setUserOnline", currentUser.getUsername()};
            writer.writeUTF(new Gson().toJson(json));
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logout(User currentUser) {
        try {
            Socket socket = new Socket("localhost",PORT);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            String [] json = {"logout",currentUser.getUsername()};
            String out = new Gson().toJson(json);
            dos.writeUTF(out);
            dos.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String changeUsername(String username) {
        if (username.isEmpty()) {
            return "Username can't be empty";
        }
        if (RegisterMenuController.isCorrectUsername(username)) {
            if (!LoginMenuController.isUsernameUsed(username)) {
                String oldUsername = currentUser.getUsername();
                currentUser.setUsername(username);
                try {
                    Socket socket = new Socket("localhost", PORT);
                    DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
                    String[] json = {"update", new Gson().toJson(currentUser)};
                    writer.writeUTF(new Gson().toJson(json));
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "true";
            }
            return "Player with username <" + username + "> already exists";
        }
        return "Username <" + username + "> has incorrect format";
    }

    public String changeNickname(String nickname) {
        String oldNickName = currentUser.getNickname();
        currentUser.setNickname(nickname);
        try {
            Socket socket = new Socket("localhost", PORT);
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            String[] json = {"update", new Gson().toJson(currentUser)};
            writer.writeUTF(new Gson().toJson(json));
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Your nickname <" + oldNickName + "> changed to <" + nickname + "> successfully";
    }

    public String changePassword(String oldPassword, String newPassword) throws NoSuchAlgorithmException {
        if (oldPassword.isEmpty()) {
            return "Password can't be empty";
        }
        if (newPassword.isEmpty()) {
            return "New password can't be empty";
        }
        String oldPasswordToShow = oldPassword;
        if (oldPassword.equals(currentUser.getPassword())) {
            if (RegisterMenuController.isPasswordWeak(newPassword).equals("true")) {
                if (newPassword.equals(oldPassword)) {
                    LoginMenuController.setNewPassword(currentUser.getUsername(), newPassword, newPassword);
                    try {
                        Socket socket = new Socket("localhost", PORT);
                        DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
                        String[] json = {"update", new Gson().toJson(currentUser)};
                        writer.writeUTF(new Gson().toJson(json));
                        writer.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return "true";
                }
                return "You should choose a new password";
            }
            return RegisterMenuController.isPasswordWeak(newPassword);
        }
        return "Your password <" + oldPasswordToShow + "> is wrong";
    }

    public String changeEmail(String email) {
        if (email.isEmpty()) {
            return "Email can't be empty";
        }
        if (RegisterMenuController.isEmailFormatCorrect(email.toUpperCase())) {
            if (!LoginMenuController.isEmailUsed(email)) {
                currentUser.setEmail(email);
                try {
                    Socket socket = new Socket("localhost", PORT);
                    DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
                    String[] json = {"update", new Gson().toJson(currentUser)};
                    writer.writeUTF(new Gson().toJson(json));
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "true";
            }
            return "This email is already used";
        }
        return "The email format is incorrect";
    }

    public String changeSlogan(String slogan) {
        if (slogan.isEmpty()) {
            return "Slogan can't be empty";
        }
        currentUser.setSlogan(slogan);
        try {
            Socket socket = new Socket("localhost", PORT);
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            String[] json = {"update", new Gson().toJson(currentUser)};
            writer.writeUTF(new Gson().toJson(json));
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "true";
    }

    public String removeSlogan() {
        currentUser.setSlogan("");// null or ""?
        try {
            Socket socket = new Socket("localhost", PORT);
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            String[] json = {"update", new Gson().toJson(currentUser)};
            writer.writeUTF(new Gson().toJson(json));
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Your slogan removed successfully";
    }

    public String displayHighScore() {
        return "Your highScore is : " + currentUser.getHighScore();
    }

    public String displaySlogan() {
        if (currentUser.getSlogan() != null)
            return "Your slogan is : " + currentUser.getSlogan();
        else
            return "You don't have a slogan";
    }

    public String displayRank() {
        currentUser.setRank(LoginMenuController.playerRank(currentUser));
        return "Your rank is : " + currentUser.getRank();
    }

    public String displayAll() {
        return "User Information :" +
                "\n\tUsername = " + currentUser.getUsername() +
                "\n\tNickname = " + currentUser.getNickname() +
                "\n\tSlogan = " + currentUser.getSlogan() +
                "\n\tHighScore = " + currentUser.getHighScore() +
                "\n\tRank = " + currentUser.getRank() +
                "\n\tE-mail = " + currentUser.getEmail();
    }
}
