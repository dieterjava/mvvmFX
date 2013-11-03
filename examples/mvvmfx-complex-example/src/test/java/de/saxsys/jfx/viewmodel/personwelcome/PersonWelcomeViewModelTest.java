package de.saxsys.jfx.viewmodel.personwelcome;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.saxsys.jfx.exampleapplication.model.Repository;
import de.saxsys.jfx.exampleapplication.viewmodel.personwelcome.PersonWelcomeViewModel;

public class PersonWelcomeViewModelTest {

	@Test
	public void testWelcomeStringInViewModel() throws Exception {
		// You should mock this repo.
		Repository repository = new Repository();

		final PersonWelcomeViewModel personWelcomeViewModel = new PersonWelcomeViewModel(
				repository);
		personWelcomeViewModel.setPersonId(repository.getPersons().get(0)
				.getId());
		assertEquals(
				"Willkommen Herr Casall, oder wollen Sie Alexander genannt werden?",
				personWelcomeViewModel.welcomeStringProperty().get());

		assertEquals(repository.getPersons().get(0).getId(),
				personWelcomeViewModel.getPersonId());
	}
	
	@Test
	public void welcomeStringForFemalePersonIsDifferent() throws Exception {
		Repository repository = new Repository();
		
		final PersonWelcomeViewModel personWelcomeViewModel = new PersonWelcomeViewModel(
				repository);
		personWelcomeViewModel.setPersonId(repository.getPersons().get(2)
				.getId());
		assertEquals(
				"Willkommen Frau Schulze, oder wollen Sie Anna genannt werden?",
				personWelcomeViewModel.welcomeStringProperty().get());

		assertEquals(repository.getPersons().get(2).getId(),
				personWelcomeViewModel.getPersonId());
	}
}
