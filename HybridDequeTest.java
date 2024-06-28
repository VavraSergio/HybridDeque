import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class HybridDequeTest {

  @Test
  void testConstructor() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    assertEquals(0, deque1.size());

    assertEquals(null, deque1.peekFirst());
    assertEquals(null, deque1.peekLast());
  }

  @Test
  void testClear() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    assertEquals(true, deque1.offerLast(1));
    assertEquals(true, deque1.offerLast(2));
    deque1.clear();
    assertEquals(0, deque1.size());
  }

  @Test
  void testOfferLast() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    assertEquals(0, deque1.size());
    assertNull(deque1.peekFirst());
    assertNull(deque1.peekLast());

    assertThrows(NullPointerException.class, () -> {
      deque1.offerLast(null);
    });

    assertEquals(true, deque1.offerLast(1));
    assertEquals(true, deque1.offerLast(2));
    assertEquals(true, deque1.offerLast(3));

    assertEquals(1, deque1.peekFirst());
    assertEquals(3, deque1.peekLast());
    assertEquals(3, deque1.size());

    HybridDeque<Integer> deque2 = new HybridDeque<>();
    for (int i = 0; i <= 63; i++) {
      deque2.offerLast(i);
    }
    assertEquals(64, deque2.size());
    deque2.offerLast(65);
    assertEquals(65, deque2.peekLast());
    assertEquals(65, deque2.size());


  }

  @Test
  void testOfferFirst() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    assertEquals(0, deque1.size());
    assertNull(deque1.peekFirst());
    assertNull(deque1.peekLast());

    assertThrows(NullPointerException.class, () -> {
      deque1.offerFirst(null);
    });
    assertEquals(true, deque1.offerFirst(1));
    assertEquals(true, deque1.offerFirst(2));
    assertEquals(true, deque1.offerFirst(3));

    assertEquals(3, deque1.peekFirst());
    assertEquals(1, deque1.peekLast());
    assertEquals(3, deque1.size());

    // Test block size increase.
    HybridDeque<Integer> deque2 = new HybridDeque<>();
    for (int i = 0; i <= 63; i++) {
      deque2.offerFirst(i);
    }
    assertEquals(64, deque2.size());
    deque2.offerFirst(65);
    assertEquals(65, deque2.peekFirst());
    assertEquals(65, deque2.size());

  }

  @Test
  void testPollFirst() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    assertEquals(true, deque1.offerFirst(1));
    assertEquals(true, deque1.offerFirst(2));
    assertEquals(true, deque1.offerFirst(3));

    assertEquals(3, deque1.pollFirst());
    assertEquals(2, deque1.peekFirst());
    deque1.pollFirst();
    deque1.pollFirst();
    assertEquals(null, deque1.pollFirst());
  }

  @Test
  void testPollLastExtra() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    HybridDeque.setBlockSize(4);
    deque1.offerFirst(1);
    deque1.offerFirst(2);
    deque1.offerFirst(3);
    deque1.offerFirst(4);
    deque1.offerFirst(5);
    deque1.offerFirst(6);
    deque1.offerFirst(7);
    assertEquals(1, deque1.pollLast());
    assertEquals(2, deque1.pollLast());
    assertEquals(3, deque1.pollLast());
    assertEquals(4, deque1.pollLast());
    assertEquals(5, deque1.pollLast());
    assertEquals(6, deque1.pollLast());




  }

  @Test
  void testPollLast() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    assertEquals(true, deque1.offerLast(1));
    assertEquals(true, deque1.offerLast(2));
    assertEquals(true, deque1.offerLast(3));

    assertEquals(3, deque1.pollLast());
    assertEquals(2, deque1.peekLast());
    deque1.pollLast();
    deque1.pollLast();
    assertEquals(null, deque1.pollLast());
  }

  @Test
  void testEquals() {
    // Test same object
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    assertEquals(true, deque1.equals(deque1));

    // Make some failures
    HybridDeque<Integer> deque2 = new HybridDeque<>();
    assertEquals(false, deque1.equals(null));
    deque2.offerLast(4);
    assertEquals(false, deque1.equals(deque2));

    deque1.offerLast(3);
    assertEquals(false, deque1.equals(deque2));

    deque1.clear();
    deque2.clear();

    deque1.offerLast(4);
    deque1.offerLast(5);
    deque2.offerLast(4);
    deque2.offerLast(5);

    assertEquals(true, deque1.equals(deque2));

    deque1.offerLast(2);
    deque2.offerLast(3);

    assertEquals(false, deque1.equals(deque2));


  }

  @Test
  void testRemoveFirstOccurenceMissing() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    deque1.offerFirst(1);
    deque1.offerFirst(3);
    deque1.offerFirst(2);
    deque1.offerFirst(4);
    deque1.offerFirst(4);
    assertEquals(false, deque1.removeFirstOccurrence(5));

  }

  @Test
  void testRemoveFirstOccurence() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    deque1.offerFirst(1);
    deque1.offerFirst(3);
    deque1.offerFirst(4);
    deque1.offerFirst(4);

    deque1.removeFirstOccurrence(4);
    deque1.removeFirstOccurrence(4);

    assertEquals(2, deque1.size());

  }

  void testRemoveFirstOccurenceExtraBlocks() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    deque1.offerFirst(1);
    deque1.offerFirst(3);
    deque1.offerFirst(4);
    deque1.offerFirst(4);
    deque1.offerFirst(4);
    deque1.offerFirst(4);
    deque1.offerFirst(4);



    deque1.removeFirstOccurrence(4);
    deque1.removeFirstOccurrence(4);

    assertEquals(2, deque1.size());
  }

  @Test
  void testRemoveLastOccurenceMissing() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    deque1.offerFirst(1);
    deque1.offerFirst(3);
    deque1.offerFirst(2);
    deque1.offerFirst(4);
    deque1.offerFirst(4);
    assertEquals(false, deque1.removeLastOccurrence(5));
  }

  @Test
  void testRemoveLastOccurence() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    deque1.offerLast(1);
    deque1.offerLast(3);
    deque1.offerLast(2);
    deque1.offerLast(4);
    deque1.offerLast(4);
    assertEquals(true, deque1.removeLastOccurrence(2));
    assertEquals(4, deque1.size());
  }

  @Test
  void testRemoveLastOccurenceExtraBlock() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    deque1.offerFirst(1);
    deque1.offerFirst(2);
    deque1.offerFirst(4);
    deque1.offerFirst(5);
    deque1.offerFirst(6);
    deque1.offerFirst(7);
    deque1.offerFirst(8);
    deque1.offerFirst(9);
    deque1.offerFirst(2);
    deque1.offerFirst(2);
    deque1.offerFirst(2);

    deque1.offerFirst(2);

    Iterator<Integer> iterator = deque1.iterator();


    assertEquals(true, deque1.removeLastOccurrence(2));
    assertEquals(11, deque1.size());
  }

  @Test
  void testRemoveLastOccurencePassNull() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    deque1.offerFirst(1);
    assertThrows(NullPointerException.class, () -> {
      deque1.removeLastOccurrence(null);
    });
  }

  @Test
  void testRemoveFirstOccurencePassNull() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    deque1.offerFirst(1);
    assertThrows(NullPointerException.class, () -> {
      deque1.removeFirstOccurrence(null);
    });
  }

  @Test
  void testRemoveWithoutNext() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    deque1.offerFirst(1);
    deque1.offerFirst(3);
    deque1.offerFirst(2);
    deque1.offerFirst(4);
    Iterator<Integer> iterator = deque1.iterator();
    iterator.next();
    iterator.remove();
    assertThrows(IllegalStateException.class, () -> {
      iterator.remove();
    });
  }

  @Test
  void testRemoveWithoutNextReverse() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    deque1.offerFirst(1);
    deque1.offerFirst(3);
    deque1.offerFirst(2);
    deque1.offerFirst(4);
    Iterator<Integer> iterator = deque1.descendingIterator();
    iterator.next();
    iterator.remove();
    assertThrows(IllegalStateException.class, () -> {
      iterator.remove();

    });
    deque1.addAll(deque1);
    HybridDeque.setBlockSize(64);
  }


  @Test
  void testRemoveWithAlternating() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    HybridDeque.setBlockSize(4);
    for (int i = 0; i < 12; i++) {
      deque1.offerLast(i);
    }

    for (int i = 0; i < 12; i++) {
      deque1.offerFirst(i);
    }
  }

  @Test
  void testRemoveNullPointer() {
    HybridDeque<Integer> deque1 = new HybridDeque<>();
    deque1.offerFirst(1);
    deque1.offerFirst(3);
    deque1.removeFirstOccurrence(1);
    deque1.removeFirstOccurrence(3);
    deque1.removeFirstOccurrence(1);
  }

  @Test
  public void testDescendingIteratorNoElements() {
    HybridDeque<Integer> deque = new HybridDeque<>();
    Iterator<Integer> iterator = deque.descendingIterator();

    assertFalse(iterator.hasNext());

    try {
      iterator.next();
      fail("Expected NoSuchElementException");
    } catch (NoSuchElementException e) {

    }
  }

  @Test
  public void testIteratorNoElements() {
    HybridDeque<Integer> deque = new HybridDeque<>();
    Iterator<Integer> iterator = deque.iterator();

    assertFalse(iterator.hasNext());

    try {
      iterator.next();
      fail("Expected NoSuchElementException");
    } catch (NoSuchElementException e) {

    }
  }

  @Test
  public void testRemoveMethodEndOfDeque() {
    HybridDeque<Integer> deque = new HybridDeque<>();
    deque.offerLast(1);
    deque.offerLast(2);
    deque.offerLast(3);
    Iterator<Integer> iterator = deque.iterator();
    while (iterator.hasNext()) {
      iterator.next();
    }
    iterator.remove();



  }
}


