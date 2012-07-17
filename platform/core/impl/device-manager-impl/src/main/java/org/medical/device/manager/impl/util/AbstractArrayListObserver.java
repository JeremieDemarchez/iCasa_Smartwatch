package org.medical.device.manager.impl.util;

import java.util.Collection;

public abstract class AbstractArrayListObserver<E> implements
		ArrayListObserver<E> {

	@Override
	public void onAdd(E element) {
		// do nothing
	}

	@Override
	public void onAdd(int index, E element) {
		// do nothing
	}

	@Override
	public void onAddAll(Collection<? extends E> elements) {
		// do nothing
	}

	@Override
	public void onAddAll(int index, Collection<? extends E> elements) {
		// do nothing
	}

	@Override
	public void onClear() {
		// do nothing
	}

	@Override
	public void onRemove(int index) {
		// do nothing
	}

	@Override
	public void onRemove(Object obj) {
		// do nothing
	}

	@Override
	public void onRemoveAll(Collection<?> c) {
		// do nothing
	}

	@Override
	public void onRetainAll(Collection<?> c) {
		// do nothing
	}

	@Override
	public void onSet(int index, E element) {
		// do nothing
	}

	@Override
	public void onSubList(int fromIndex, int toIndex) {
		// do nothing
	}

}