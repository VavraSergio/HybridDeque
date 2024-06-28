import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Doubly-linked-list implementation of the java.util.Deque interface. This implementation is more
 * space-efficient than Java's LinkedList class for large collections because each node contains a
 * block of elements instead of only one. This reduces the overhead required for next and previous
 * node references.
 *
 * 
 * <p>This implementation does not allow null's to be added to the collection. Adding a null will
 * result in a NullPointerException.
 * 
 */
public class HybridDeque<E> extends AbstractDeque<E> {

  /*
   * IMPLEMENTATION NOTES ----------------------------------
   *
   * The list of blocks is never empty, so leftBlock and rightBlock are never equal to null. The
   * list is not circular.
   *
   * A deque's first element is at leftBlock.elements[leftIndex]
   * 
   * and its last element is at rightBlock.elements[rightIndex].
   * 
   * The indices, leftIndex and rightIndex are always in the range:
   * 
   * 0 <= index < BLOCK_SIZE
   *
   * And their exact relationship is:
   * 
   * (leftIndex + size - 1) % BLOCK_SIZE == rightIndex
   *
   * Whenever leftBlock == rightBlock, then:
   * 
   * leftIndex + size - 1 == rightIndex
   *
   * However, when leftBlock != rightBlock, the leftIndex and rightIndex become indices into
   * distinct blocks and either may be larger than the other.
   *
   * Empty deques have:
   * 
   * size == 0
   * 
   * leftBlock == rightBlock
   * 
   * leftIndex == CENTER + 1
   * 
   * rightIndex == CENTER
   * 
   * Checking for size == 0 is the intended way to see whether the Deque is empty.
   * 
   * 
   * (Comments above are a lightly modified version of comments in Python's deque implementation:
   * https://github.com/python/cpython/blob/v3.11.2/Modules/_collectionsmodule.c
   * https://docs.python.org/3.11/license.html)
   * 
   */

  private static int BLOCK_SIZE = 4;
  private static int CENTER = (BLOCK_SIZE - 1) / 2;

  private Cursor leftCursor;
  private Cursor rightCursor;
  private int size;


  /**
   * DO NOT MODIFY THIS METHOD. This will be used in grading/testing to modify the default block
   * size..
   *
   * @param blockSize The new block size
   */
  protected static void setBlockSize(int blockSize) {
    HybridDeque.BLOCK_SIZE = blockSize;
    HybridDeque.CENTER = (blockSize - 1) / 2;
  }


  /**
   * Doubly linked list node (or block) containing an array with space for multiple elements.
   */
  private class Block {
    private E[] elements;
    private Block next;
    private Block prev;

    /**
     * Block Constructor.
     *
     * @param prev Reference to previous block, or null if this is the first
     * @param next Reference to next block, or null if this is the last
     */
    @SuppressWarnings("unchecked")
    public Block(Block prev, Block next) {
      this.elements = (E[]) (new Object[BLOCK_SIZE]);
      this.next = next;
      this.prev = prev;
    }

  }

  /**
   * Many of the complications of implementing this Deque class are related to the fact that there
   * are two pieces of information that need to be maintained to track a position in the deque: a
   * block reference and the index within that block. This class combines those two pieces of
   * information and provides the logic for moving forward and backward through the deque structure.
   * 
   * 
   * <p>NOTE: The provided cursor class is *immutable*: 
   * once a Cursor object is created, it cannot be
   * modified. Incrementing forward or backward involves creating new Cursor objects at the required
   * location. Immutable objects can be cumbersome to work with, but they prevent coding errors
   * caused by accidentally aliasing mutable objects.
   */
  private class Cursor {
    private final Block block;
    private final int index;

    public Cursor(HybridDeque<E>.Block block, int index) {
      this.block = block;
      this.index = index;
    }

    /**
     * Increment the cursor, crossing a block boundary if necessary.
     *
     * @return A new cursor at the next position, or null if there are no more valid positions.
     */
    private Cursor next() {

      if (index == BLOCK_SIZE - 1) { // We need to cross a block boundary

        return new Cursor(block.next, 0);

      } else { // Just move one spot forward in the current block
        return new Cursor(block, index + 1);
      }
    }

    /**
     * Decrement the cursor, crossing a block boundary if necessary.
     *
     * @return A new cursor at the previous position, or null if there is no previous position.
     */
    private Cursor prev() {
      if (index == 0) { // We need to cross a block boundary

        return new Cursor(block.prev, BLOCK_SIZE - 1);

      } else { // Just move one spot back in the current block.
        return new Cursor(block, index - 1);
      }
    }

    /**
     * Return the element stored at this cursor.
     */
    public E get() {
      return block.elements[index];
    }

    /**
     * Set the element at this cursor.
     */
    public void set(E item) {
      block.elements[index] = item;
    }

  }

  // ----------------------------------------------------
  // ADD UNIMPLEMENTED DEQUE METHODS HERE.
  // (You Don't need to provide JavaDoc comments for inherited methods. They
  // will inherit appropriate comments from Deque.)

  // -------------------------------------------------
  // METHODS THAT NEED TO BE IMPLEMENTED FOR PART 1:
  //
  // constructor
  // clear
  // offerLast
  // offerFirst
  // peekFirst
  // peekLast
  // pollFirst
  // pollLast
  // equals
  // iterator (without removal)
  // descendingIterator (without removal)

  // -------------------------------------------------
  // METHODS THAT NEED TO BE IMPLEMENTED FOR PART 2:
  //
  // removeFirstOccurrence
  // removeLastOccurrence
  // remove methods for iterators

  /**
   * A class to create the HybridDeque.
   * 
   *
   *
   * <p>Creates the HybridDeque
   */
  public HybridDeque() {
    Block headBlock = new Block(null, null);
    leftCursor = new Cursor(headBlock, CENTER + 1);
    rightCursor = new Cursor(headBlock, CENTER);

    headBlock.next = headBlock;
    headBlock.prev = headBlock;

    size = 0;
  }


  @Override
  public void clear() {
    size = 0;

    Block clearBlock = new Block(null, null);

    clearBlock.next = clearBlock;
    clearBlock.prev = clearBlock;

    leftCursor = new Cursor(clearBlock, CENTER + 1);
    rightCursor = new Cursor(clearBlock, CENTER);
  }


  @Override
  public boolean offerLast(E e) {
    if (e == null) {
      throw new NullPointerException();
    }

    if (rightCursor.index == BLOCK_SIZE - 1) {
      Block newBlock = new Block(rightCursor.block, null);
      rightCursor.block.next = newBlock;
      rightCursor = new Cursor(newBlock, 0);
    } else {
      rightCursor = rightCursor.next();
    }

    rightCursor.set(e);

    size++;

    return true;
  }


  @Override
  public boolean offerFirst(E e) {
    if (e == null) {
      throw new NullPointerException();
    }

    if (leftCursor.index == 0) {
      Block newBlock = new Block(null, leftCursor.block);
      leftCursor.block.prev = newBlock;
      leftCursor = new Cursor(newBlock, BLOCK_SIZE - 1);
    } else {
      leftCursor = leftCursor.prev();
    }

    leftCursor.set(e);

    size++;

    return true;
  }


  @Override
  public E pollFirst() {
    if (size == 0) {
      return null;
    }

    size--;
    E e = leftCursor.get();
    leftCursor.set(null);
    leftCursor = leftCursor.next();
    return e;
  }


  @Override
  public E pollLast() {

    if (size == 0) {
      return null;
    }
    size--;

    E e = rightCursor.get();
    rightCursor.set(null);
    rightCursor = rightCursor.prev();

    return e;
  }


  @Override
  public E peekFirst() {
    if (size == 0) {
      return null;
    }

    return leftCursor.get();

  }


  @Override
  public E peekLast() {
    if (size == 0) {
      return null;
    }

    return rightCursor.get();

  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AbstractDeque == false) {
      return false; 
    }
    if (this == obj) {
      return true;
    }

    HybridDeque<?> other = (HybridDeque<?>) obj;

    if (size != other.size) {
      return false;
    }

    Iterator<E> iterator = iterator();
    @SuppressWarnings("unchecked")
    Iterator<E> otherIterator = (Iterator<E>) other.iterator();

    while (iterator.hasNext()) {
      E thisElement = iterator.next();
      Object otherElement = otherIterator.next();

      if (!Objects.equals(thisElement, otherElement)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean removeFirstOccurrence(Object o) {
    if (o == null) {
      throw new NullPointerException();
    }

    Iterator<E> iterator = iterator();
    while (iterator.hasNext()) {
      E e = iterator.next();
      if (e.equals(o)) {
        iterator.remove();
        return true;
      }
    }

    return false;
  }


  @Override
  public boolean removeLastOccurrence(Object o) {
    if (o == null) {
      throw new NullPointerException();
    }

    Iterator<E> iterator = descendingIterator();
    while (iterator.hasNext()) {
      E e = iterator.next();
      if (e.equals(o)) {
        iterator.remove();
        return true;
      }
    }

    return false;
  }


  @Override
  public Iterator<E> iterator() {
    return new HybridDequeIterator();
  }

  private class HybridDequeIterator implements Iterator<E> {

    private Cursor iteratorCursor = leftCursor;
    private int index = 0;
    private boolean shouldRemove = false;

    @Override
    public boolean hasNext() {

      return iteratorCursor != null && iteratorCursor.block != null && index < size;
    }

    @Override
    public E next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      index++;

      E element = iteratorCursor.get();

      iteratorCursor = iteratorCursor.next();
      shouldRemove = false;
      return element;
    }

    @Override
    public void remove() {
      if (shouldRemove) {
        throw new IllegalStateException();
      }

      Cursor currentCursor = iteratorCursor.prev();
      for (int i = index; i < size; i++) {
        Cursor nextCursor = currentCursor.next();

        E nextElement = nextCursor.get();
        currentCursor.set(nextElement);
        currentCursor = nextCursor;
      }

      currentCursor.set(null);


      shouldRemove = true;
      size--;

    }

  }


  @Override
  public Iterator<E> descendingIterator() {
    // TODO Auto-generated method stub
    return new HybridDequedescendingIterator();
  }

  private class HybridDequedescendingIterator implements Iterator<E> {

    private Cursor iteratorCursor = rightCursor;
    private int index = 0;
    private boolean shouldRemove = false;

    @Override
    public boolean hasNext() {

      return iteratorCursor != null && iteratorCursor.block != null && index < size
          && iteratorCursor.get() != null;
    }

    @Override
    public E next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      index++;

      E element = iteratorCursor.get();

      iteratorCursor = iteratorCursor.prev();

      shouldRemove = false;
      return element;
    }

    @Override
    public void remove() {
      if (shouldRemove) {
        throw new IllegalStateException();
      }

      Cursor currentCursor = iteratorCursor.next();
      for (int i = index; i < size; i++) {
        Cursor nextCursor = currentCursor.next();


        E nextElement = nextCursor.get();
        currentCursor.set(nextElement);
        currentCursor = nextCursor;
      }

      shouldRemove = true;
      size--;
      rightCursor = rightCursor.prev();
    }
  }


  @Override
  public boolean addAll(Collection<? extends E> c) {
    // TODO Auto-generated method stub
    return false;
  }


  @Override
  public int size() {
    return size;
  }

}
