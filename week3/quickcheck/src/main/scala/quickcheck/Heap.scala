package quickcheck

import common._

trait IntHeap extends Heap {
  override type A = Int
  override def ord: Ordering.Int.type = scala.math.Ordering.Int
}

// http://www.brics.dk/RS/96/37/BRICS-RS-96-37.pdf

// Figure 1, page 3
trait Heap {
  type H // type of a heap
  type A // type of an element
  def ord: Ordering[A] // ordering on elements

  def empty: H // the empty heap
  def isEmpty(h: H): Boolean // whether the given heap h is empty

  def insert(x: A, h: H): H // the heap resulting from inserting x into h
  def meld(h1: H, h2: H): H // the heap resulting from merging h1 and h2

  def findMin(h: H): A // a minimum of the heap h
  def deleteMin(h: H): H // a heap resulting from deleting a minimum of h
}

// Figure 3, page 7
trait BinomialHeap extends Heap {

  type Rank = Int

  case class Node(x: A, r: Rank, c: List[Node])

  override type H = List[Node]

  protected def root(t: Node): A = t.x

  protected def rank(t: Node): Rank = t.r

  protected def link(t1: Node, t2: Node): Node = // t1.r==t2.r
    if (ord.lteq(t1.x,t2.x)) Node(t1.x, t1.r+1, t2::t1.c) else Node(t2.x, t2.r+1, t1::t2.c)

  protected def ins(t: Node, ts: H): H = ts match {
    case Nil => List(t)
    case tp::tss =>
      if (t.r < tp.r) { // t.r <= tp.r
        t::tp::tss
      } else {
        ins(link(t, tp), tss)
      }
  }

  override def empty: Nil.type = Nil

  override def isEmpty(ts: H): Boolean = ts.isEmpty

  override def insert(x: A, ts: H): List[Node] = ins(Node(x,0,Nil), ts)

  override def meld(ts1: H, ts2: H): H = (ts1, ts2) match {
    case (Nil, ts) => ts
    case (ts, Nil) => ts
    case (t1::tss1, t2::tss2) =>
      if (t1.r < t2.r) t1::meld(tss1, t2::tss2)
      else if (t2.r < t1.r) t2::meld(t1::tss1, tss2)
      else ins(link(t1, t2),meld(tss1, tss2))
  }

  override def findMin(ts: H): A = ts match {
    case Nil => throw new NoSuchElementException("min of empty heap")
    case t::Nil => root(t)
    case t::tss =>
      val x = findMin(tss)
      if (ord.lteq(root(t),x)) root(t) else x
  }

  override def deleteMin(ts: H): List[Node] = ts match {
    case Nil => throw new NoSuchElementException("delete min of empty heap")
    case t::tss =>
      def getMin(t: Node, ts: H): (Node, H) = ts match {
        case Nil => (t, Nil)
        case tp::tsp =>
          val (tq,tsq) = getMin(tp, tsp)
          if (ord.lteq(root(t),root(tq))) (t,ts) else (tq,t::tsq)
      }
      val (Node(_,_,c),tsq) = getMin(t, tss)
      meld(c.reverse, tsq)
  }
}

trait Bogus1BinomialHeap extends BinomialHeap {
  override def findMin(ts: H): A = ts match {
    case Nil => throw new NoSuchElementException("min of empty heap")
    case t::tss => root(t)
  }
}

trait Bogus2BinomialHeap extends BinomialHeap {
  override protected def link(t1: Node, t2: Node): Node = // t1.r==t2.r
    if (!ord.lteq(t1.x,t2.x)) Node(t1.x, t1.r+1, t2::t1.c) else Node(t2.x, t2.r+1, t1::t2.c)
}

trait Bogus3BinomialHeap extends BinomialHeap {
  override protected def link(t1: Node, t2: Node): Node = // t1.r==t2.r
    if (ord.lteq(t1.x,t2.x)) Node(t1.x, t1.r+1, t1::t1.c) else Node(t2.x, t2.r+1, t2::t2.c)
}

trait Bogus4BinomialHeap extends BinomialHeap {
  override def deleteMin(ts: H): List[Node] = ts match {
    case Nil => throw new NoSuchElementException("delete min of empty heap")
    case t::tss => meld(t.c.reverse, tss)
  }
}

trait Bogus5BinomialHeap extends BinomialHeap {
  override def meld(ts1: H, ts2: H): List[Node] = ts1 match {
    case Nil => ts2
    case t1::tss1 => List(Node(t1.x, t1.r, tss1 ++ ts2))
  }
}
