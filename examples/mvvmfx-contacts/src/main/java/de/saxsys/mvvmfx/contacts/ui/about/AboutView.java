package de.saxsys.mvvmfx.contacts.ui.about;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.controlsfx.control.HyperlinkLabel;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.contacts.events.OpenAboutDialogEvent;
import de.saxsys.mvvmfx.contacts.util.DialogHelper;

@Singleton
public class AboutView implements FxmlView<AboutViewModel> {
	
	@Inject
	private Stage primaryStage;
	
	@FXML
	private HyperlinkLabel librariesLabel;
	
	@InjectViewModel
	private AboutViewModel viewModel;
	
	private Parent root;
	
	@Inject
	AboutView() {
		root = FluentViewLoader.fxmlView(AboutView.class)
				.codeBehind(this)
				.load().getView();
	}
	
	public void initialize() {
		DialogHelper.initDialog(viewModel.dialogOpenProperty(), primaryStage, () -> root);
		
		librariesLabel.textProperty().bind(viewModel.librariesLabelTextProperty());
		librariesLabel.setOnAction(event -> {
			Hyperlink link = (Hyperlink) event.getSource();
			String str = link == null ? "" : link.getText();
			viewModel.onLinkClicked(str);
		});
	}
	
	@FXML
	public void openAuthorPage() {
		viewModel.openAuthorPage();
	}
	
	public void open(@Observes OpenAboutDialogEvent event) {
		viewModel.openDialog();
	}
}
