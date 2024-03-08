/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.controls.base.AbstractMFXListView;
import io.github.palexdev.materialfx.controls.cell.MFXListCell;
import io.github.palexdev.materialfx.skins.MFXListViewSkin;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.base.Theme;
import io.github.palexdev.materialfx.utils.ListChangeHelper;
import io.github.palexdev.materialfx.utils.ListChangeProcessor;
import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import io.github.palexdev.virtualizedfx.unused.simple.SimpleVirtualFlow;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Skin;

import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementation of a check listview based on VirtualizedFX.
 * <p>
 * Extends {@link AbstractMFXListView}.
 * <p></p>
 * Default cell: {@link MFXListCell}.
 * <p>
 * Default skin: {@link MFXListViewSkin}.
 * <p></p>
 * Holds the reference to the VirtualFlow used by the list, this allows to expose some
 * methods from it as delegate method thus allowing to:
 * <p> - Manually scroll by a certain amount of pixels
 * <p> - Manually scroll to a given index (also first and last)
 * <p> - Manually scroll to the given pixel value
 * <p> - Set the scrollbar's speed
 * <p> - Get the vertical or horizontal position of the list
 * <p> - Configure extra features of the VirtualFlow, {@link #features()}
 * <p> - Get the currently shown cells, or a specific cell by index
 * <p></p>
 * It's also responsible for updating the selection model in case the items list property
 * changes, or changes occur in the items list.
 */
public class MFXListView<T> extends AbstractMFXListView<T, MFXListCell<T>> {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-list-view";
	private final SimpleVirtualFlow<T, MFXListCell<T>> virtualFlow;
	private final ListChangeListener<? super T> itemsChanged = this::itemsChanged;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXListView() {
		virtualFlow = new SimpleVirtualFlow<>(
				itemsProperty(),
				null,
				Orientation.VERTICAL
		);
		initialize();
	}

	public MFXListView(ObservableList<T> items) {
		super(items);
		virtualFlow = new SimpleVirtualFlow<>(
				itemsProperty(),
				null,
				Orientation.VERTICAL
		);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	@Override
	protected void initialize() {
		super.initialize();
		getStyleClass().setAll(STYLE_CLASS);
		items.addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) oldValue.removeListener(itemsChanged);
			if (newValue != null) newValue.removeListener(itemsChanged);
		});
		getItems().addListener(itemsChanged);
	}

	protected void itemsChanged(ListChangeListener.Change<? extends T> change) {
		if (getSelectionModel().getSelection().isEmpty()) return;

		if (change.getList().isEmpty()) {
			getSelectionModel().clearSelection();
			return;
		}

		ListChangeHelper.Change c = ListChangeHelper.processChange(change, IntegerRange.of(0, Integer.MAX_VALUE));
		ListChangeProcessor updater = new ListChangeProcessor(new HashSet<>(getSelectionModel().getSelection().keySet()));
		c.processReplacement((changed, removed) -> getSelectionModel().replaceSelection(changed.toArray(new Integer[0])));
		c.processAddition((from, to, added) -> {
			updater.computeAddition(added.size(), from);
			getSelectionModel().replaceSelection(updater.getIndexes().toArray(new Integer[0]));
		});
		c.processRemoval((from, to, removed) -> {
			updater.computeRemoval(removed, from);
			getSelectionModel().replaceSelection(updater.getIndexes().toArray(new Integer[0]));
		});
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	/**
	 * Delegate method for {@link SimpleVirtualFlow#getCell(int)}.
	 */
	public MFXListCell<T> getCell(int index) {
		return virtualFlow.getCell(index);
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#getCells()}.
	 */
	public Map<Integer, MFXListCell<T>> getCells() {
		return virtualFlow.getCells();
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#scrollBy(double)}.
	 */
	public void scrollBy(double pixels) {
		virtualFlow.scrollBy(pixels);
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#scrollTo(int)}.
	 */
	public void scrollTo(int index) {
		virtualFlow.scrollTo(index);
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#scrollToFirst()}.
	 */
	public void scrollToFirst() {
		virtualFlow.scrollToFirst();
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#scrollToLast()}.
	 */
	public void scrollToLast() {
		virtualFlow.scrollToLast();
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#scrollToPixel(double)}.
	 */
	public void scrollToPixel(double pixel) {
		virtualFlow.scrollToPixel(pixel);
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#setHSpeed(double, double)}.
	 */
	public void setHSpeed(double unit, double block) {
		virtualFlow.setHSpeed(unit, block);
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#setVSpeed(double, double)}.
	 */
	public void setVSpeed(double unit, double block) {
		virtualFlow.setVSpeed(unit, block);
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#getVerticalPosition()}.
	 */
	public double getVerticalPosition() {
		return virtualFlow.getVerticalPosition();
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#getHorizontalPosition()}.
	 */
	public double getHorizontalPosition() {
		return virtualFlow.getHorizontalPosition();
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#features()}.
	 */
	public SimpleVirtualFlow<T, MFXListCell<T>>.Features features() {
		return virtualFlow.features();
	}

	//================================================================================
	// Override Methods
	//================================================================================

	@Override
	public Theme getTheme() {
		return MaterialFXStylesheets.LIST_VIEW;
	}

	@Override
	protected void setDefaultCellFactory() {
		setCellFactory(item -> new MFXListCell<>(this, item));
	}

	@Override
	public Function<T, MFXListCell<T>> getCellFactory() {
		return virtualFlow.getCellFactory();
	}

	@Override
	public ObjectProperty<Function<T, MFXListCell<T>>> cellFactoryProperty() {
		return virtualFlow.cellFactoryProperty();
	}

	@Override
	public void setCellFactory(Function<T, MFXListCell<T>> cellFactory) {
		virtualFlow.setCellFactory(cellFactory);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXListViewSkin<>(this, virtualFlow);
	}
}
