package view;

import controller.ChatMenuController;
import controller.LoginMenuController;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.User;
import model.chat.Chat;
import model.chat.ChatType;
import model.chat.Message;
import model.chat.Pair;
import view.lobby.StartMenu;

import java.awt.*;
import java.util.ArrayList;

public class ChatMenu extends Application {
    public ChatMenu(User currentUser) {
        this.currentUser = currentUser;
    }

    private TextArea textArea;
    private Button button1;
    private TextArea search = new TextArea();
    private String username = "";
    private String toAdd = "";
    private User currentUser;
    private Chat currentChat = null;
    private Pane chatSpace = null;
    private Button button = new Button("Search");
    private boolean isComingFromLobby = false;

    public boolean isComingFromLobby() {
        return isComingFromLobby;
    }

    public User getCurrentUser() {
        return currentUser;
    }


    public Chat getCurrentChat() {
        return currentChat;
    }

    public void setCurrentChat(Chat currentChat) {
        this.currentChat = currentChat;
    }

    public void setComingFromLobby(boolean comingFromLobby) {
        isComingFromLobby = comingFromLobby;
    }

    public static User userToChatWith = null;
    private static Button createGroup;
    private static Button add = new Button("add");

    @Override
    public void start(Stage stage) throws Exception {
        ChatMenuController.loadChats();
        Pane pane = new Pane();
        setBack1(pane);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        pane.setPrefSize(width, height);
        VBox chatList = getChatList(width, height, stage, pane);
        chatList.setLayoutX(0);
        chatList.setLayoutY(0);
        getSearch(pane, width, height, stage, chatList);
        pane.getChildren().add(chatList);
        pane.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.R) {
                chatSpaceUpdate(pane);
                System.out.println("refreshed");
            }
        });
        createGroup = createNewRoom(width, height, stage, pane, chatList);
        createGroup.setLayoutX(10);
        createGroup.setLayoutY(height - 100);
        pane.getChildren().add(createGroup);
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        MainMenu.stopNotif(stage);
    }

    private TextArea type() {
        TextArea textArea = new TextArea();
        textArea.setPrefSize(800, 30);
        return textArea;
    }

    private Pane getChatSpace(double width, double height, Pane pane) {
        textArea = type();
        button1 = getSend(textArea, width, height, pane);
        Pane chatSpace = new Pane();
        BackgroundImage backgroundImage = new BackgroundImage(new Image(getClass().getResource("/BackGrounds/chat.jpg").toExternalForm()), null, null, null, null);
        chatSpace.setBackground(new Background(backgroundImage));
        chatSpace.setLayoutX(350);
        chatSpace.setLayoutY(0);
        chatSpace.setPrefSize(width - 350, height);
        if (currentChat != null) {
            //todo
            ArrayList<Message> messages = currentChat.getMessages();
            if (currentChat.getChatType() == ChatType.ROOM) {
                addUser(chatSpace, pane);
            }
            VBox vBox = new VBox();
            vBox.setSpacing(15);
            vBox.setPrefSize(width - 350, height);
            for (Message message : messages) {
                handleMessage(pane, vBox, message);
            }
            setChatPLace(width, height, chatSpace, backgroundImage, vBox);
        } else {
            getSelectChat(width, chatSpace);
        }
        return chatSpace;
    }

    private void handleMessage(Pane pane, VBox vBox, Message message) {
        HBox hBox = new HBox();
        ImageView avatar = new ImageView(new Image(message.getSenderAvatar()));
        avatar.setFitWidth(50);
        avatar.setFitHeight(50);
        Text text = new Text(message.getContent());
        Text text1 = new Text(message.getSender().getUsername() + " : " + message.getSendingTime() + "✓" + message.getIsSeenChar());
        HBox hBox1 = new HBox(text1);
        text1.textProperty().addListener((observable, oldValue, newValue) -> {
            if (message.getIsSeenChar() == '✓') {
                text1.setText(message.getSender().getUsername() + " : " + message.getSendingTime() + "✓✓");
            }
        });
        Text likeTxt = new Text("");
        Text dislikeTxt = new Text("");
        Text laughTxt = new Text("");
        Text heartTxt = new Text("");
        Text heartEyesTxt = new Text("");
        likeTxt.setText("👍" + message.getReactions().get(0).getNumberOfReactions());
        dislikeTxt.setText("👎" + message.getReactions().get(1).getNumberOfReactions());
        heartTxt.setText("❤" + message.getReactions().get(2).getNumberOfReactions());
        heartEyesTxt.setText("😍" + message.getReactions().get(3).getNumberOfReactions());
        laughTxt.setText("😂" + message.getReactions().get(4).getNumberOfReactions());
        HBox hBox2 = new HBox(likeTxt, dislikeTxt, heartTxt, heartEyesTxt, laughTxt);
        hBox1.setAlignment(Pos.BOTTOM_RIGHT);
        VBox vBox1 = new VBox(text, hBox1, hBox2);
        vBox1.setSpacing(5);
        if (message.getSender() == currentUser) {
            text.setTextAlignment(TextAlignment.RIGHT);
            hBox.getChildren().addAll(vBox1, avatar);
            hBox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            text.setTextAlignment(TextAlignment.LEFT);
            hBox.getChildren().addAll(avatar, vBox1);
            hBox.setAlignment(Pos.CENTER_LEFT);
        }
        vBox.getChildren().add(hBox);
        text.setStyle("-fx-font: 24 arial;");
        getContextMenu(message, text, pane);
    }

    private void setChatPLace(double width, double height, Pane chatSpace, BackgroundImage backgroundImage, VBox vBox) {
        ScrollPane scrollPane = new ScrollPane(vBox);
        scrollPane.setPrefSize(width - 350, height);
        VBox vBox1 = new VBox(scrollPane);
        vBox1.setBackground(new Background(backgroundImage));
        vBox1.setLayoutX(0);
        vBox1.setLayoutY(0);
        vBox1.setPrefSize(width - 350, height - 100);
        HBox hBox = new HBox(textArea, button1);
        hBox.setLayoutX(5);
        hBox.setLayoutY(height - 100);
        hBox.setSpacing(20);
        chatSpace.getChildren().add(vBox1);
        chatSpace.getChildren().add(hBox);
    }

    private void getContextMenu(Message message, Text text, Pane pane) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem edit = new MenuItem("Edit");
        MenuItem deleteForMe = new MenuItem("Delete for me");
        MenuItem deleteForAll = new MenuItem("Delete for all");
        MenuItem like = new MenuItem("👍");
        MenuItem dislike = new MenuItem("👎");
        MenuItem heart = new MenuItem("❤");
        MenuItem heartEyes = new MenuItem("😍");
        MenuItem laugh = new MenuItem("😂");
        MenuItem addFriend = new MenuItem("Add Friend");
        contextMenu.getItems().addAll(edit, deleteForMe, deleteForAll, like, dislike, heart, heartEyes, laugh, addFriend);
        text.setOnContextMenuRequested(contextMenuEvent -> {
            contextMenu.show(text, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
        });
        editHandler(message, text, pane, edit);
        deleteForMe.setOnAction(actionEvent -> {
            //todo
        });
        deleteForAll.setOnAction(actionEvent -> {
            //todo
            currentChat.getMessages().remove(message);
            chatSpaceUpdate(pane);
        });
        likeReact(message, pane, like);
        dislikeReact(message, pane, dislike);
        heartReact(message, pane, heart);
        heartEyeReact(message, pane, heartEyes);
        laughReact(message, pane, laugh);
        addFriend.setOnAction(actionEvent -> {
            //todo
            if (message.getSender() == currentUser) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You can't add yourself!");
                alert.show();
            } else {
                message.getSender().getWaitingForYouToAccept().add(currentUser);
                currentUser.getWaitingForThemToAccept().add(message.getSender());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Friend added successfully!");
                alert.show();
                //todo send a message to the receiver
            }
        });
    }

    private void editHandler(Message message, Text text, Pane pane, MenuItem edit) {
        edit.setOnAction(actionEvent -> {
            //todo
            textArea.setText(message.getContent());
            button1.setOnMouseClicked(mouseEvent -> {
                if (message.getSender() == currentUser) {
                    message.setContent(textArea.getText());
                    text.setText(textArea.getText());
                    textArea.setText("");
                    button1.setOnMouseClicked(mouseEvent1 -> {
                        Message message1 = new Message(currentUser, currentChat, textArea.getText());
                        chatSpaceUpdate(pane);
                    });
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("You can't edit this message!");
                    alert.show();
                }
            });
        });
    }

    private void likeReact(Message message, Pane pane, MenuItem like) {
        like.setOnAction(actionEvent -> {
            //todo
            for (Pair pair : Pair.getPairs()) {
                if (pair.getMessage() == message && pair.getUser() == currentUser) {
                    if (!pair.isUserReacted()) {
                        pair.setUserReacted(true);
                        message.getReactions().get(0).setNumberOfReactions(message.getReactions().get(0).getNumberOfReactions() + 1);
                        chatSpaceUpdate(pane);
                    }
                }
            }
        });
    }

    private void dislikeReact(Message message, Pane pane, MenuItem dislike) {
        dislike.setOnAction(actionEvent -> {
            //todo
            for (Pair pair : Pair.getPairs()) {
                if (pair.getMessage() == message && pair.getUser() == currentUser) {
                    if (!pair.isUserReacted()) {
                        pair.setUserReacted(true);
                        message.getReactions().get(1).setNumberOfReactions(message.getReactions().get(1).getNumberOfReactions() + 1);
                        chatSpaceUpdate(pane);
                    }
                }
            }
        });
    }

    private void heartReact(Message message, Pane pane, MenuItem heart) {
        heart.setOnAction(actionEvent -> {
            //todo
            for (Pair pair : Pair.getPairs()) {
                if (pair.getMessage() == message && pair.getUser() == currentUser) {
                    if (!pair.isUserReacted()) {
                        pair.setUserReacted(true);
                        message.getReactions().get(2).setNumberOfReactions(message.getReactions().get(2).getNumberOfReactions() + 1);
                        chatSpaceUpdate(pane);
                    }
                }
            }
        });
    }

    private void heartEyeReact(Message message, Pane pane, MenuItem heartEyes) {
        heartEyes.setOnAction(actionEvent -> {
            //todo
            for (Pair pair : Pair.getPairs()) {
                if (pair.getMessage() == message && pair.getUser() == currentUser) {
                    if (!pair.isUserReacted()) {
                        pair.setUserReacted(true);
                        message.getReactions().get(3).setNumberOfReactions(message.getReactions().get(3).getNumberOfReactions() + 1);
                        chatSpaceUpdate(pane);
                    }
                }
            }
        });
    }

    private void laughReact(Message message, Pane pane, MenuItem laugh) {
        laugh.setOnAction(actionEvent -> {
            //todo
            for (Pair pair : Pair.getPairs()) {
                if (pair.getMessage() == message && pair.getUser() == currentUser) {
                    if (!pair.isUserReacted()) {
                        pair.setUserReacted(true);
                        message.getReactions().get(4).setNumberOfReactions(message.getReactions().get(4).getNumberOfReactions() + 1);
                        chatSpaceUpdate(pane);
                    }
                }
            }
        });
    }

    private void chatSpaceUpdate(Pane pane) {
        pane.getChildren().remove(chatSpace);
        chatSpace = getChatSpace(pane.getWidth(), pane.getHeight(), pane);
        pane.getChildren().add(chatSpace);
        if (chatSpace.getChildren().contains(add)) {
            add.toFront();
        }
    }

    private static void getSelectChat(double width, Pane chatSpace) {
        Text text = new Text("Select a chat");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setLayoutX(width / 2);
        text.setLayoutY(100);
        chatSpace.getChildren().add(text);
        text.prefHeight(200);
        text.prefWidth(300);
        text.setFill(Color.RED);
        text.setStyle("-fx-font: 24 arial;");
    }

    private Button getSend(TextArea textArea, double width, double height, Pane pane) {
        Button send = new Button("Send");
        send.setOnMouseClicked(mouseEvent -> {
            Message message = new Message(currentUser, currentChat, textArea.getText());
            chatSpaceUpdate(pane);
        });
        return send;
    }

    private void addUser(Pane chatSpace, Pane pane) {
        add.setLayoutX(5);
        add.setLayoutY(5);
        add.setPrefSize(50, 20);
        chatSpace.getChildren().add(add);
        add.setOnMouseClicked(mouseEvent -> {
            if (currentChat.getAdmin() == currentUser) {
                if (!pane.getChildren().contains(button)) {
                    pane.getChildren().add(button);
                }
                button.setOnMouseClicked(mouseEvent1 -> {
                    toAdd = search.getText();
                    String answer = ChatMenuController.addUserToChat(toAdd, currentChat);
                    if (answer.equals("userAdded")) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText(answer);
                        alert.showAndWait();
                        pane.getChildren().remove(button);
                        chatSpaceUpdate(pane);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText(answer);
                        alert.showAndWait();
                    }
                });
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You are not admin of this chat");
                alert.showAndWait();
            }
        });
    }

    private void setBack1(Pane pane) {
        BackgroundImage backgroundImage1 = new BackgroundImage(new Image(getClass().getResource("/BackGrounds/backGround.png").toExternalForm()), null, null, null, null);
        pane.setBackground(new Background(backgroundImage1));
    }

    private void getSearch(Pane pane, double width, double height, Stage stage, VBox chatList) {
        search.setPromptText("Search");
        search.setPrefSize(150, 20);
        search.setLayoutX(200);
        search.setLayoutY(0);
        search.setOnMouseClicked(mouseEvent -> {
            button.setPrefSize(80, 20);
            button.setLayoutX(200);
            button.setLayoutY(50);
            if (!pane.getChildren().contains(button))
                pane.getChildren().add(button);
            button.setOnMouseClicked(actionEvent -> {
                username = search.getText();
                if (LoginMenuController.isUsernameUsed(username)) {
                    if (currentChat == null || currentChat.getChatType() != ChatType.ROOM) {
                        userToChatWith = LoginMenuController.getUser(username);
                        Chat chat1 = new Chat(currentUser, userToChatWith, "pv");
                        ChatMenuController.saveChats();
                        currentChat = chat1;
                        if (!currentUser.getChats().contains(chat1)) {
                            currentUser.getChats().add(chat1);
                        }
                        if (userToChatWith.getChats() == null) {
                            userToChatWith.setChats(new ArrayList<>());
                        }
                        if (!userToChatWith.getChats().contains(chat1)) {
                            userToChatWith.getChats().add(chat1);
                        }
                        chatSpaceUpdate(pane);
                        updateChatList(width, height, stage, pane, chatList);
                    } else if (currentChat != null && currentChat.getChatType() == ChatType.ROOM) {
                        String answer = ChatMenuController.addUserToChat(toAdd, currentChat);
                        if (answer.equals("userAdded")) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText(answer);
                            alert.showAndWait();
                            pane.getChildren().remove(button);
                            chatSpaceUpdate(pane);
                            ChatMenuController.saveChats();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText(answer);
                            alert.showAndWait();
                        }
                        for (User user : currentChat.getUsers()) {
                            System.out.println(user.getUsername());
                        }
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("User not found");
                    alert.showAndWait();
                }
            });
        });
        pane.getChildren().add(search);
    }

    private Button getBackButton(double width, double height, Stage stage) {
        Button button = new Button("Back");
        button.setPrefSize(50, 30);
        button.setLayoutX(5);
        button.setLayoutY(5);
        button.setOnAction(actionEvent -> {
            try {
                if (!isComingFromLobby)
                    new MainMenu(currentUser).start(stage);
                else
                    new StartMenu(currentUser.getGameRequest()).start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return button;
    }

    private VBox getChatList(double width, double height, Stage stage, Pane pane) {
        VBox vBox = new VBox();
        Button back = getBackButton(width, height, stage);
        Text text = new Text("Chat List");
        vBox.getChildren().add(back);
        vBox.setPrefSize(200, height);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setWrappingWidth(200);
        vBox.getChildren().add(text);
        ArrayList<Chat> chats = getChats();
        for (Chat chat : chats) {
            Text chatText = new Text(chat.getName());
            Button enter = new Button("Enter");
            enter.setOnMouseClicked(mouseEvent -> {
                currentChat = chat;
                for (Message message : chat.getMessages()) {
                    if (message.getSender() != currentUser) {
                        message.setIsSeenChar('✓');
                    }
                }
                chatSpace = getChatSpace(width, height, pane);
                if (!pane.getChildren().contains(chatSpace))
                    pane.getChildren().add(chatSpace);
            });
            HBox hBox = new HBox(enter, chatText);
            hBox.setSpacing(10);
            chatText.setWrappingWidth(100);
            vBox.getChildren().add(hBox);
        }
        ScrollPane scrollPane = new ScrollPane(vBox);
        VBox wrapper = new VBox(scrollPane);
        wrapper.setPrefSize(200, height);
        wrapper.setOnMouseClicked(mouseEvent -> {
            currentChat = null;
            chatSpaceUpdate(pane);
        });
        return wrapper;
    }

    private void updateChatList(double width, double height, Stage stage, Pane pane, VBox chatList) {
        VBox vBox = getChatList(width, height, stage, pane);
        pane.getChildren().remove(chatList);
        pane.getChildren().add(vBox);
        createGroup.toFront();
    }

    private ArrayList<Chat> getChats() {
        ArrayList<Chat> chats = new ArrayList<>();
        if (currentUser.getChats() != null) {
            for (Chat chat : currentUser.getChats()) {
                chats.add(chat);
            }
        } else {
            currentUser.setChats(new ArrayList<>());
        }
        return chats;
    }

    private Button createNewRoom(double width, double height, Stage stage, Pane pane, VBox chatList) {
        Button button = new Button("Create new room");
        button.setOnMouseClicked(mouseEvent -> {
            int cnt = 0;
            for (Chat chat : currentUser.getChats()) {
                if (chat.getChatType().equals(ChatType.ROOM)) {
                    cnt++;
                }
            }
            Chat chat = new Chat(currentUser, new ArrayList<User>(), ("room " + cnt + " by " + currentUser.getNickname()));
            ChatMenuController.saveChats();
            ChatMenuController.giveTheChatToUser(chat, currentUser);
        });
        button.setOnMouseExited(mouseEvent -> {

            updateChatList(width, height, stage, pane, chatList);
        });
        return button;
    }
}
