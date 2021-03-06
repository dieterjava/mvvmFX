package de.saxsys.mvvmfx.internal.viewloader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import net.jodah.typetools.TypeResolver;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewModel;

/**
 * This class encapsulates reflection related utility operations specific for loading of views.
 * 
 * @author manuel.mauky
 */
public class ViewLoaderReflectionUtils {
	
	
	
	/**
	 * Returns the {@link java.lang.reflect.Field} of the viewModel for a given view type and viewModel type. If there
	 * is no annotated field for the viewModel in the view the returned Optional will be empty.
	 *
	 * @param viewType
	 *            the type of the view
	 * @param viewModelType
	 *            the type of the viewModel
	 * @return an Optional that contains the Field when the field exists.
	 */
	public static Optional<Field> getViewModelField(Class<? extends View> viewType, Class<?> viewModelType) {
		List<Field> viewModelFields = Arrays.stream(viewType.getDeclaredFields())
				.filter(field -> field.isAnnotationPresent(InjectViewModel.class))
				.filter(field -> field.getType().isAssignableFrom(viewModelType))
				.collect(Collectors.toList());
		if (viewModelFields.isEmpty()) {
			return Optional.empty();
		}
		if (viewModelFields.size() > 1) {
			throw new RuntimeException("The View <" + viewType + "> may only define one viewModel but there were <"
					+ viewModelFields.size() + "> viewModel fields!");
		}
		return Optional.of(viewModelFields.get(0));
	}
	
	/**
	 * This method is used to get the ViewModel instance of a given view/codeBehind.
	 *
	 * @param view
	 *            the view instance where the viewModel will be looked for.
	 * @param <ViewType>
	 *            the generic type of the View
	 * @param <ViewModelType>
	 *            the generic type of the ViewModel
	 * @return the ViewModel instance or null if no viewModel could be found.
	 */
	@SuppressWarnings("unchecked")
	public static <ViewType extends View<? extends ViewModelType>, ViewModelType extends ViewModel> ViewModelType getExistingViewModel(
			ViewType view) {
		final Class<?> viewModelType = TypeResolver.resolveRawArgument(View.class, view.getClass());
		Optional<Field> fieldOptional = getViewModelField(view.getClass(), viewModelType);
		if (fieldOptional.isPresent()) {
			Field field = fieldOptional.get();
			return ReflectionUtils
					.accessField(field, () -> (ViewModelType) field.get(view), "Can't get the viewModel of type <"
							+ viewModelType + ">");
		} else {
			return null;
		}
	}
	
	/**
	 * Injects the given viewModel instance into the given view. The injection will only happen when the class of the
	 * given view has a viewModel field that fulfills all requirements for the viewModel injection (matching types, no
	 * viewModel already existing ...).
	 *
	 * @param view
	 * @param viewModel
	 */
	public static void injectViewModel(final View view, ViewModel viewModel) {
		if (viewModel == null) {
			return;
		}
		final Optional<Field> fieldOptional = getViewModelField(view.getClass(), viewModel.getClass());
		if (fieldOptional.isPresent()) {
			Field field = fieldOptional.get();
			ReflectionUtils.accessField(field, () -> {
				Object existingViewModel = field.get(view);
				if (existingViewModel == null) {
					field.set(view, viewModel);
				}
			}, "Can't inject ViewModel of type <" + viewModel.getClass()
					+ "> into the view <" + view + ">");
		}
	}
	
	/**
	 * This method is used to create and inject the ViewModel for a given View instance. The ViewModel is only created
	 * if the View has a suitable field for the ViewModel.
	 * 
	 * If a ViewModel was created OR there was already a ViewModel set in the View, this viewModel instance is returned
	 * via an Optional.
	 * 
	 * @param view
	 *            the view instance.
	 * @param <V>
	 *            the generic type of the View.
	 * @param <VM>
	 *            the generic type of the ViewModel.
	 * @return an Optional containing the ViewModel if it was created or already existing. Otherwise the Optional is
	 *         empty.
	 */
	@SuppressWarnings("unchecked")
	public static <V extends View<? extends VM>, VM extends ViewModel> Optional<VM> createAndInjectViewModel(
			final V view) {
		final Class<?> viewModelType = TypeResolver.resolveRawArgument(View.class, view.getClass());
		
		if (viewModelType == ViewModel.class) {
			return Optional.empty();
		}
		if (viewModelType == TypeResolver.Unknown.class) {
			return Optional.empty();
		}
		
		final Optional<Field> fieldOptional = getViewModelField(view.getClass(), viewModelType);
		if (fieldOptional.isPresent()) {
			Field field = fieldOptional.get();
			
			Object viewModel = ReflectionUtils.accessField(field, () -> {
				Object existingViewModel = field.get(view);
				
				if (existingViewModel != null) {
					return existingViewModel;
				} else {
					final Object newViewModel = DependencyInjector.getInstance().getInstanceOf(viewModelType);
					
					field.set(view, newViewModel);
					
					return newViewModel;
				}
			}, "Can't inject ViewModel of type <" + viewModelType
					+ "> into the view <" + view + ">");
			
			if (viewModel == null) {
				return Optional.empty();
			}
			
			try {
				return Optional.of((VM) viewModel);
			} catch (ClassCastException e) {
				return Optional.empty();
			}
		}
		
		return Optional.empty();
	}
	
	/**
	 * Creates a viewModel instance for a View type. The type of the view is determined by the given view instance.
	 *
	 * For the creation of the viewModel the {@link DependencyInjector} is used.
	 * 
	 * @param view
	 *            the view instance that is used to find out the type of the ViewModel
	 * @param <ViewType>
	 *            the generic view type
	 * @param <ViewModelType>
	 *            the generic viewModel type
	 * @return the viewModel instance or <code>null</code> if the viewModel type can't be found or the viewModel can't
	 *         be created.
	 */
	@SuppressWarnings("unchecked")
	public static <ViewType extends View<? extends ViewModelType>, ViewModelType extends ViewModel> ViewModelType createViewModel(
			ViewType view) {
		final Class<?> viewModelType = TypeResolver.resolveRawArgument(View.class, view.getClass());
		if (viewModelType == ViewModel.class) {
			return null;
		}
		if (TypeResolver.Unknown.class == viewModelType) {
			return null;
		}
		return (ViewModelType) DependencyInjector.getInstance().getInstanceOf(viewModelType);
	}
	
	
	/**
	 * If a ViewModel has a method with the signature <code>public void initialize()</code> it will be invoked. If no
	 * such method is available nothing happens.
	 * 
	 * @param viewModel
	 *            the viewModel that's initialize method (if available) will be invoked.
	 * @param <ViewModelType>
	 *            the generic type of the ViewModel.
	 */
	public static <ViewModelType extends ViewModel> void initializeViewModel(ViewModelType viewModel) {
		try {
			final Method initMethod = viewModel.getClass().getMethod("initialize");
			
			AccessController.doPrivileged((PrivilegedAction) () -> {
				try {
					return initMethod.invoke(viewModel);
				} catch (InvocationTargetException | IllegalAccessException e) {
					throw new IllegalStateException("mvvmFX wasn't able to call the initialize method of ViewModel ["
							+ viewModel + "].", e);
				}
			});
		} catch (NoSuchMethodException e) {
			// it's perfectly fine that a ViewModel has no initialize method.
		}
	}
}
