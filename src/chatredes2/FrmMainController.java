/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatredes2;

import entities.CChat;
import entities.CPeer;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import servers.Servidor;

/**
 * FXML Controller class
 *
 * @author esteban
 */
public class FrmMainController implements Initializable {

    @FXML
    private TabPane tabPanel;

    @FXML
    private TextField txtNickname;

    @FXML
    private TableColumn colUser;

    @FXML
    private TableColumn colHostname;

    @FXML
    private TableView tablePeers;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
	Servidor srv = Servidor.getInstance();
	srv.setGUI(this);

	Bindings.bindBidirectional(txtNickname.textProperty(), Servidor.getInstance().nicknameProperty());

	tablePeers.setRowFactory(
		new Callback<TableView<CPeer>, TableRow<CPeer>>() {
		    @Override
		    public TableRow<CPeer> call(TableView<CPeer> tableView) {
			final TableRow<CPeer> row = new TableRow<>();
			final ContextMenu rowMenu = new ContextMenu();
			MenuItem NewChat = new MenuItem("Nuevo chat");
			NewChat.setOnAction((ActionEvent event) -> {
			    CPeer peer = row.getItem();
			    NewChat(peer);
			});
			MenuItem addToChat = new MenuItem("Añadir al chat");
			addToChat.setOnAction((ActionEvent event) -> {
			    CPeer peer = row.getItem();
			    AddToChat(peer);
			});
			rowMenu.getItems().addAll(NewChat, addToChat);

			// only display context menu for non-null items:
			row.contextMenuProperty().bind(
				Bindings.when(Bindings.isNotNull(row.itemProperty()))
				.then(rowMenu)
				.otherwise((ContextMenu) null));
			return row;
		    }

		    private void NewChat(CPeer peer) {
			CChat chat = new CChat();
			Servidor srv = Servidor.getInstance();
			srv.getChats().put(chat.getChatID(), chat);
			chat.peersProperty().add(peer);
			chat.peersProperty().add(Servidor.getInstance().getMe());
			ChatTab ctl_tab = new ChatTab(peer.getNickname(), chat);
			Tab tab = new Tab(peer.getNickname());
			tab.setContent(ctl_tab);
			tabPanel.getTabs().add(tab);
		    }

		    private void AddToChat(CPeer peer) {
			// Hay que buscar el chat activo:
			for (Tab tab : tabPanel.getTabs()) {
			    if (tab.isSelected()) {
				ChatTab ct = (ChatTab) tab.getContent();
				ct.Añadir(peer);
				//ct.getChat().getPeers().add(peer);
			    }
			}
		    }
		});

	colUser.setCellValueFactory(new PropertyValueFactory("nickname"));
	colHostname.setCellValueFactory(new PropertyValueFactory("HostAddress"));
	Bindings.bindBidirectional(tablePeers.itemsProperty(), Servidor.getInstance().peersProperty());
    }

    public void OpenChat(String chatID) {
	CChat chat;
	Servidor srv = Servidor.getInstance();
	chat = srv.getChats().get(chatID);
	
	Platform.runLater(() -> {
	    ChatTab ctl_tab = new ChatTab("", chat);
	    Tab tab = new Tab(new Date().toString());
	    tab.setContent(ctl_tab);
	    tabPanel.getTabs().add(tab);
	});
    }

}
