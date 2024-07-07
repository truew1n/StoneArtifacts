package me.truewin.StoneArtifacts;

import java.util.Comparator;

public class Range<T> {
	
	private T start;
	private T end;
	
	public Range(T start, T end) {
		this.start = start;
		this.end = end;
	}
	
	public boolean inRange(T value, Comparator<T> comparator) {
		return (comparator.compare(this.end, value) >= 0 && comparator.compare(this.start, value) <= 0);
	}
	
	public T getStart() {
		return this.start;
	}
	
	public T getEnd() {
		return this.end;
	}
	
	public String toString() {
		return "<" + this.start + ";" + this.end + ">";
	}
}
