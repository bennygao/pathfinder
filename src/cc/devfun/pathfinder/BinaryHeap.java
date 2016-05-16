package cc.devfun.pathfinder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

public final class BinaryHeap<E> {
	/**
	 * The default capacity for a binary heap.
	 */
	private final static int DEFAULT_CAPACITY = 13;

	/**
	 * The number of elements currently in this heap.
	 */
	int m_size; // package scoped for testing

	/**
	 * The elements in this heap.
	 */
	Object[] m_elements; // package scoped for testing

	/**
	 * The comparator used to order the elements
	 */
	Comparator<E> m_comparator; // package scoped for testing

	/**
	 * Constructs a new BinaryHeap that will use the given comparator to order
	 * its elements.
	 * 
	 * @param comparator
	 *            the comparator used to order the elements, null means use
	 *            natural order
	 */
	public BinaryHeap(Comparator<E> comparator) {
		this();
		m_comparator = comparator;
	}

	/**
	 * Constructs a new BinaryHeap.
	 * 
	 * @param capacity
	 *            the initial capacity for the heap
	 * @param comparator
	 *            the comparator used to order the elements, null means use
	 *            natural order
	 * @throws IllegalArgumentException
	 *             if capacity is <= 0
	 */
	public BinaryHeap(int capacity, Comparator<E> comparator) {
		this(capacity);
		m_comparator = comparator;
	}

	/**
	 * Constructs a new minimum or maximum binary heap
	 * 
	 * @param isMinHeap
	 *            if true the heap is created as a minimum heap; otherwise, the
	 *            heap is created as a maximum heap
	 */
	public BinaryHeap() {
		this(DEFAULT_CAPACITY);
	}

	/**
	 * Constructs a new minimum or maximum binary heap with the specified
	 * initial capacity.
	 * 
	 * @param capacity
	 *            the initial capacity for the heap. This value must be greater
	 *            than zero.
	 * @throws IllegalArgumentException
	 *             if capacity is <= 0
	 */
	public BinaryHeap(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("invalid capacity");
		}

		// +1 as 0 is noop
		m_elements = new Object[capacity + 1];
	}

	// -----------------------------------------------------------------------
	/**
	 * Clears all elements from queue.
	 */
	public void clear() {
		Arrays.fill(m_elements, null);
		m_size = 0;
	}

	public int indexOf(Object obj) {
		for (int i = 1; i <= m_size; ++i) {
			if (m_elements[i].equals(obj)) {
				return i;
			}
		}

		return -1;
	}

	@SuppressWarnings("unchecked")
	public E getAt(int idx) {
		return (E) m_elements[idx];
	}

	/**
	 * Tests if queue is empty.
	 * 
	 * @return true if queue is empty; false otherwise.
	 */
	public boolean isEmpty() {
		return m_size == 0;
	}

	/**
	 * Tests if queue is full.
	 * 
	 * @return true if queue is full; false otherwise.
	 */
	public boolean isFull() {
		// +1 as element 0 is noop
		return m_elements.length == m_size + 1;
	}

	/**
	 * Inserts an element into queue.
	 * 
	 * @param element
	 *            the element to be inserted
	 */
	public void insert(Object element) {
		if (isFull()) {
			grow();
		}
		// percolate element to it's place in tree
		percolateUpMinHeap(element);
	}

	/**
	 * Returns the element on top of heap but don't remove it.
	 * 
	 * @return the element at top of heap
	 * @throws NoSuchElementException
	 *             if isEmpty() == true
	 */
	@SuppressWarnings("unchecked")
	public E peek() throws NoSuchElementException {
		if (isEmpty()) {
			return null;
		} else {
			return (E) m_elements[1];
		}
	}

	/**
	 * Returns the element on top of heap and remove it.
	 * 
	 * @return the element at top of heap
	 * @throws NoSuchElementException
	 *             if isEmpty() == true
	 */
	@SuppressWarnings("unchecked")
	public E pop() {
		if (isEmpty()) {
			return null;
		}

		final Object result = peek();
		m_elements[1] = m_elements[m_size--];

		// set the unused element to 'null' so that the garbage collector
		// can free the object if not used anywhere else.(remove reference)
		m_elements[m_size + 1] = null;

		if (m_size != 0) {
			// percolate top element to it's place in tree
			percolateDownMinHeap(1);
		}

		return (E) result;
	}
	
	public void modifiedAt(int idx) {
		Object curr = getAt(idx);
		Object node = null;
		
		/* 与父节点比较，比父节点小，就向前调整 */
		int p = idx >> 1;
		if (p > 0) {
			node = getAt(p);
			if (compare(curr, node) < 0) {
				percolateUpMinHeap(idx);
				return;
			}
		}
		
		/* 与子节点比较，比子节点大，就向后调整 */
		p = idx << 1;
		for (int i = 0; i < 2; ++i) {
			p += i;
			if (p < m_size) {
				node = getAt(p);
				if (compare(curr, node) > 0) {
					percolateDownMinHeap(idx);
					return;
				}
			}
		}		
	}

	/**
	 * Percolates element down heap from the position given by the index.
	 * 
	 * Assumes it is a minimum heap.
	 * 
	 * @param index
	 *            the index for the element
	 */
	protected void percolateDownMinHeap(final int index) {
		final Object element = m_elements[index];
		int hole = index;

		while ((hole * 2) <= m_size) {
			int child = hole * 2;

			// if we have a right child and that child can not be percolated
			// up then move onto other child
			if (child != m_size
					&& compare(m_elements[child + 1], m_elements[child]) < 0) {
				child++;
			}

			// if we found resting place of bubble then terminate search
			if (compare(m_elements[child], element) >= 0) {
				break;
			}

			m_elements[hole] = m_elements[child];
			hole = child;
		}

		m_elements[hole] = element;
	}

	/**
	 * Percolates element up heap from the position given by the index.
	 * 
	 * Assumes it is a minimum heap.
	 * 
	 * @param index
	 *            the index of the element to be percolated up
	 */
	protected void percolateUpMinHeap(final int index) {
		int hole = index;
		Object element = m_elements[hole];
		while (hole > 1 && compare(element, m_elements[hole / 2]) < 0) {
			// save element that is being pushed down
			// as the element "bubble" is percolated up
			final int next = hole / 2;
			m_elements[hole] = m_elements[next];
			hole = next;
		}
		m_elements[hole] = element;
	}

	/**
	 * Percolates a new element up heap from the bottom.
	 * 
	 * Assumes it is a minimum heap.
	 * 
	 * @param element
	 *            the element
	 */
	protected void percolateUpMinHeap(final Object element) {
		m_elements[++m_size] = element;
		percolateUpMinHeap(m_size);
	}

	/**
	 * Compares two objects using the comparator if specified, or the natural
	 * order otherwise.
	 * 
	 * @param a
	 *            the first object
	 * @param b
	 *            the second object
	 * @return -ve if a less than b, 0 if they are equal, +ve if a greater than
	 *         b
	 */
	@SuppressWarnings("unchecked")
	private int compare(Object a, Object b) {
		if (m_comparator != null) {
			return m_comparator.compare((E) a, (E) b);
		} else {
			return ((Comparable<E>) a).compareTo((E) b);
		}
	}

	/**
	 * Increases the size of the heap to support additional elements
	 */
	protected void grow() {
		final Object[] elements = new Object[m_elements.length * 2];
		System.arraycopy(m_elements, 0, elements, 0, m_elements.length);
		m_elements = elements;
	}

	/**
	 * Returns a string representation of this heap. The returned string is
	 * similar to those produced by standard JDK collections.
	 * 
	 * @return a string representation of this heap
	 */
	public String toString() {
		final StringBuffer sb = new StringBuffer();

		sb.append("[ ");

		for (int i = 1; i < m_size + 1; i++) {
			if (i != 1) {
				sb.append(", ");
			}
			sb.append(m_elements[i]);
		}

		sb.append(" ]");

		return sb.toString();
	}

	/**
	 * Adds an object to this heap. Same as {@link #insert(Object)}.
	 * 
	 * @param object
	 *            the object to add
	 * @return true, always
	 */
	public boolean add(Object object) {
		insert(object);
		return true;
	}

	/**
	 * Returns the number of elements in this heap.
	 * 
	 * @return the number of elements in this heap
	 */
	public int size() {
		return m_size;
	}

	public static void main(String[] args) throws Exception {
		BinaryHeap<Integer> heap = new BinaryHeap<Integer>();
		heap.add(10);
		heap.add(1);
		heap.add(30);
		heap.add(2);
		heap.add(3);
		for (int i = 100; i >= 0; --i) {
			heap.add(i);
		}

		Integer v;
		while ((v = heap.pop()) != null) {
			System.out.println(v);
		}
	}
}