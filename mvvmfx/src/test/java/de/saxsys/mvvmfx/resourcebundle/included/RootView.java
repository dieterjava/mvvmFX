package de.saxsys.mvvmfx.resourcebundle.included;

import de.saxsys.mvvmfx.FxmlView;
import javafx.fxml.FXML;

/**
 * @author manuel.mauky
 */
public class RootView implements FxmlView<RootViewModel> {
	
	@FXML
	public IncludedView includedViewController;
	
}
