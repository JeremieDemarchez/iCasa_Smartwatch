/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.device.manager.impl.util;

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