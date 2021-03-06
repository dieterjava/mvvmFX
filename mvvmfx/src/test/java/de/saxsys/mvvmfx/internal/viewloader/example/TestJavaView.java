package de.saxsys.mvvmfx.internal.viewloader.example;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.JavaView;

/**
 * This class is used as example View class that is written in pure java.
 */
public class TestJavaView extends VBox implements JavaView<TestViewModel>, Initializable {
	@InjectViewModel
	public TestViewModel viewModel;
	
	public ResourceBundle resourceBundle;
	
	public boolean viewModelWasNull = true;
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
		viewModelWasNull = viewModel == null;
	}
}
