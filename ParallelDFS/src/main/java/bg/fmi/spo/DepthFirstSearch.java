package bg.fmi.spo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DepthFirstSearch {
//    static final Set<Integer> visited = Collections.newSetFromMap(new ConcurrentHashMap<>());
//    static final Set<Integer> visited = Collections.synchronizedSet(new HashSet<>());
//    static final Set<Integer> visited = new CopyOnWriteArraySet<>(); -- do not use
    static int n = 10000;
    static int p = 8;
    static boolean[][] adjacencyMatrix = new boolean[n][n];
    static boolean[][] adjacencyMatrix2 = new boolean[][] {
            {false, true, true, false, false},
            {true, false, false, false, false},
            {true, false, false, false, false},
            {false, false, false, false, true},
            {false, false, false, true, false}
    };

    static boolean[][] getAdjacencyMatrix3 = new boolean[][] {
        {false, true, false, false, false, true},
        {false, false, false, true, true, false},
        {false, false, false, true, false, true},
        {false, true, true, false, false, true},
        {false, true, false, false, false, false},
        {false, false, false, false, false, false},
    };

    static Map<Integer, Integer> labelEdgeMap = new HashMap<>();
    static Map<Integer, List<Integer>> labelVertexMap = new HashMap<>();
//    static final Set<Integer> visited = new ConcurrentSkipListSet<>();
    static Set<Integer> visited = Collections.newSetFromMap(new ConcurrentHashMap<>());
//    static final Set<Integer> notVisited = new ConcurrentSkipListSet<>();
    static Set<Integer> notVisited = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void main(String[] args) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for(int i = 0; i < n; i++) {
            for (int c = 0; c < n; c++) {
                adjacencyMatrix[i][c] = random.nextBoolean();
            }
        }

//        startSearch(Integer.parseInt(args[0]));
//        startSearch(p);
        labelEdges();
        IntStream.range(0, n).forEach(notVisited::add);
//        System.out.println(labelMap);
//        parallelTraverse(Integer.parseInt(args[0]));
        while(p > 0) {
            IntStream.range(0, 10).forEach(i -> {
                parallelTraverse(p--);
            });
            System.out.println();
        }
//        System.out.println(labelVertexMap);
    }

    private static void labelEdges() {
        int counter = 0;
        for(int i = 0; i < n; i++) {
            labelVertexMap.put(i, new LinkedList<>());
            for(int c = 0; c < n; c++) {
                if(adjacencyMatrix[i][c]) {
//                    System.out.println(i + " " + c);
                    if(!labelEdgeMap.containsKey(i)) {
                        labelEdgeMap.put(i, counter++);
                    }
                    if(!labelEdgeMap.containsKey(c)) {
                        labelEdgeMap.put(c, counter++);
                    }
                }
            }
        }
    }

    private static void parallelTraverse(int p) {
        List<Thread> threads = new LinkedList<>();
        long start = System.currentTimeMillis();
        for(int i = 0; i < p; i++) {
            Thread thread = new Thread(new ShanoRunnable(labelEdgeMap.get(i))); //DepthFirstSearch::dfs
            threads.add(thread);
//            int startNode = 0;
            thread.start();
        }

//        threads.forEach(thread -> {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
        System.out.println(System.currentTimeMillis() - start);
    }

    private static class ShanoRunnable implements Runnable {
        private int startNode;
        private ThreadLocalRandom random = ThreadLocalRandom.current();
        public ShanoRunnable(int startNode) {
            this.startNode = startNode;
        }

        @Override
        public void run() {
            //dfsStart(startNode);
//            labelMap.put(labelMap.get(startNode), random.nextInt());
            long start = System.currentTimeMillis();
            startDfs(labelEdgeMap.get(startNode));

//            IntStream.range(0, n).forEach(node -> {
//                if(!visited.contains(node)) {
//                    startDfs(node);
//                }
//            });

            notVisited.forEach(this::startDfs);

//            while()
            System.out.println(Thread.currentThread().getName() + " completed in: " + (System.currentTimeMillis() - start));
        }

        private void startDfs(Integer node) {
//            Stack stack;
//            4: stack.push(v);
//            5: while !stack.empty() do
//                6: curr = stack.pop();
//            7: visited[curr] = true;
//            8: for each neighbor w of curr do
//                9: if !visited[w] then
//            10: visited[w] = true; . Early marking
//            11: //Avoids other threads pushing
//            12: //this vertex to their stacks
//            13: labels[w] = labels[curr];
//            14: stack.push(w);
//            15: if visited[w] then . Merge Labels
//            16: // visited before curr is visited
//            17: // Merge labels
//            18: L1 = labels[w]
//            19: L2 = labels[curr]
//            20: labelEquivMap[L1].add[L2]
            Stack<Integer> stack = new Stack<>();
            stack.push(node);
            while(!stack.isEmpty()) {
                Integer current = stack.pop();
                visited.add(current);
                notVisited.remove(current);
//                System.out.println(Thread.currentThread().getName() + " " + current);
                for(int i = 0; i < n; i++) {
                    if(!visited.contains(i) && adjacencyMatrix[current][i]) {
                        visited.add(i);
                        notVisited.remove(current);
//                        labelVertexMap.get(current).add(i);
                        labelEdgeMap.put(i, labelEdgeMap.get(current));
                        stack.push(i);
//                        System.out.println(Thread.currentThread().getName() + " " + i);
                    }
//                    if(visited.contains(i)) {
//                        int neighborLabel = labelMap.get(i);
//                        int currentLabel = labelMap.get(current);
//
//                    }
                }
            }
        }
    }


    public static void startSearch(int p) {
        List<Integer> nodeList = IntStream.range(0, n).boxed().collect(Collectors.toList());
        Collections.shuffle(nodeList);

        long start = System.currentTimeMillis();
        List<Thread> threads = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        for(int i = 0; i < p; i++) {
//            Thread thread = new Thread(new SearchRunnable(nodeList.get(i)));
            Thread thread = new Thread(new SearchRunnable(0, stack));
            thread.start();
            threads.add(thread);
        }
//        threads.forEach(thread -> {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });

        System.out.println(System.currentTimeMillis() - start);
    }

    private static class SearchRunnable implements Runnable {
        int startNode;
        Stack<Integer> stack;
        public SearchRunnable(int startNode, Stack<Integer> stack) {
            this.startNode = startNode;
            this.stack = stack;
        }
        @Override
        public void run() {
//            dfs(adjacencyMatrix, startNode);
            stackDfs(adjacencyMatrix2, stack, startNode);
        }
    }

    public static void stackDfs(boolean[][] matrix, Stack<Integer> stack,  int node) {
        stack.push(node);
        visited.add(node);
        long start = System.currentTimeMillis();
        while(!stack.isEmpty()) {
            Integer current = stack.pop();
            for(int i = 0; i < matrix.length; i++) {
//                synchronized (matrix) {
                if (!visited.contains(i) && matrix[current][i]) {
                    System.out.println(Thread.currentThread().getName() + " " + i);
                    stack.push(i);
                    visited.add(i);
                }
//                }
            }
        }
        System.out.println(Thread.currentThread().getName() + " completed in: " + (System.currentTimeMillis() - start));
    }


    public static void dfs(boolean[][] matrix, int currentNode) {
//        System.out.println(Thread.currentThread().getName() + " " + currentNode);

        visited.add(currentNode);
        for(int i = 0; i < matrix.length; i++) {
            if(!visited.contains(i) && matrix[currentNode][i]) {
//                synchronized (visited) {
                dfs(matrix, i);
//                }
            }
        }
    }
}
