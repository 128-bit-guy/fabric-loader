package net.fabricmc.loader.util;

import java.util.*;

public class TopologicalSorter <T> {
	private Map<T, Integer> map;
	private ArrayList<T> list;
	private int[] color;
	private Set<Integer>[] dependencies;
	private ArrayList<T> sorted;
 	@SuppressWarnings("unchecked")
	public TopologicalSorter(Collection<T> s) {
		map = new HashMap<>();
		list = new ArrayList<>();
		list.ensureCapacity(s.size());
		for(T t : s) {
			map.put(t, list.size());
			list.add(t);
		}
		color = new int[s.size()];
		for(int i = 0; i < s.size(); ++i) {
			color[i] = 0;
		}
		dependencies = new Set[s.size()];
		for(int i = 0; i < s.size(); ++i){
			dependencies[i] = new HashSet<>();
		}
		sorted = new ArrayList<>();
		sorted.ensureCapacity(s.size());
	}

	public void addAfterDependency(T dependent, T dependency) {
 		if(!map.containsKey(dependent)) {
 			throw new IllegalArgumentException("Original collection did not contain dependent object");
		}
 		if(!map.containsKey(dependency)) {
 			throw new IllegalArgumentException("Original collection did not contain dependency object");
		}
 		int x = map.get(dependent);
 		int y = map.get(dependency);
 		dependencies[x].add(y);
	}

	public void addBeforeDependency(T dependent, T dependency) {
		if(!map.containsKey(dependent)) {
			throw new IllegalArgumentException("Original collection did not contain dependent object");
		}
		if(!map.containsKey(dependency)) {
			throw new IllegalArgumentException("Original collection did not contain dependency object");
		}
 		addAfterDependency(dependency, dependent);
	}

	private void dfs(int i) {
 		if(color[i] == 2) {
 			return;
		} else if(color[i] == 1) {
 			DependencyLoopException ex = new DependencyLoopException();
 			ex.loopInfo.add(list.get(i));
 			throw ex;
		}
 		color[i] = 1;
 		for(int obj : dependencies[i]) {
 			try {
				dfs(obj);
			} catch (DependencyLoopException ex) {
 				if(ex.complete) {
 					throw ex;
				}
 				if(ex.loopInfo.contains(list.get(i))) {
 					ex.complete = true;
				} else {
					ex.loopInfo.add(list.get(i));
				}
 				throw ex;
			}
		}
 		color[i] = 2;
 		sorted.add(list.get(i));
	}

	public ArrayList<T> sort() {
 		try {
			for (int i = 0; i < color.length; ++i) {
				if (color[i] == 0) {
					dfs(i);
				}
			}
		} catch (DependencyLoopException ex) {
 			StringBuilder sb = new StringBuilder();
 			sb.append("Found loop in dependencies: \n");
 			for(Object obj : ex.loopInfo) {
 				sb.append(obj).append('\n');
			}
 			throw new IllegalArgumentException(sb.toString());
		}
 		return sorted;
	}
}
