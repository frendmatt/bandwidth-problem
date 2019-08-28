import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class Bandwidth {

    public static void main (String[] args) {
        final long startTime = System.currentTimeMillis();
        Scanner input = null;
        try {
            input = new Scanner(new File("test.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Can't find test.txt");
        }
        int numVertices = input.nextInt();
        int numEdges = input.nextInt();
        Info solution = new Info(numVertices);
        LinkedList[] adjList = new LinkedList[numVertices];
        for (int i = 0; i < numVertices; i++) {
            adjList[i] = new LinkedList<Integer>();
        }
        for (int i = 0; i < numEdges; i++) {
            int v1 = input.nextInt();
            int v2 = input.nextInt();
            adjList[v1 - 1].add(v2 - 1);
            adjList[v2 - 1].add(v1 - 1);
        }


        int lowerBound = 0;
        for (int i = 0; i < adjList.length - 1; i++) {
            if (adjList[i].size() > lowerBound) {
                if (adjList[i].size() % 2 != 0) {
                    lowerBound = (adjList[i].size() / 2) + 1;
                } else {
                    lowerBound = adjList[i].size() / 2;
                }
            }
        }
        int[] currentPermutation = new int[numVertices];
        backTrack(currentPermutation, solution, adjList, -1, lowerBound);
        System.out.println("Minimum Bandwidth: " +solution.getMinMaxBandwidth());
        int[] print = new int[numVertices];
        for (int i = 0; i < numVertices; i++) {
            System.out.print(" " + solution.getSolution()[i] + " ");
        }
        final long endTime = System.currentTimeMillis();
        System.out.println();
        System.out.print("Completed in: " + ((endTime - startTime)/1000.0) + " seconds");
    }

    static int[] constructCandidates (int[] currentPermutation, int currentPos) {
        ArrayList<Integer> temp = new ArrayList<Integer>();
        if (currentPos == currentPermutation.length - 1) {
            for (int i = currentPermutation[0]; i <= currentPermutation.length; i++) {
                temp.add(i);
            }
        } else {
            for (int i = 1; i <= currentPermutation.length; i++) {
                temp.add(i);
            }
        }
        for (int i = 0; i < currentPos; i++) {
            temp.remove((Object) currentPermutation[i]);
        }
        int[] candidates = new int[temp.size()];
        for (int i = 0; i < temp.size(); i++) {
            candidates[i] = temp.get(i);
        }
        return candidates;
    }

    static boolean isASolution (int[] currentPermutation, int currentPos) {
        return currentPermutation.length - 1 == currentPos;
    }

    static int processSolution (int[] currentPermutation, int minMaxBandwidth, LinkedList[] adjList, int currentPos) {
        int max = 0;
        for (int i = 0; i < currentPos; i++) {
            for (int j = (i + 1); j < currentPos + 1; j++) {
                if (adjList[currentPermutation[i] - 1].contains((Object) (currentPermutation[j] - 1))) {
                    int bandwidth = j - i;
                    if (bandwidth > minMaxBandwidth) {
                        return (currentPermutation.length + 3);
                    } else if (bandwidth > max) {
                        max = bandwidth;
                    }
                }
            }
        }
        return max;
    }

    static boolean isPartialSolution(int[] currentPermutation, int minMaxBandwidth, LinkedList[] adjList, int currentPos) {
        boolean[] processed = new boolean[currentPermutation.length];
        for (int i = 0; i < currentPos; i++) {
            int numEdges = adjList[currentPermutation[i] - 1].size();
            for (int j = 0; j < i; j++) {
                if (adjList[currentPermutation[i] - 1].contains((Object) (currentPermutation[j] - 1)) && processed[currentPermutation[j] - 1] == true) {
                    numEdges--;
                }
            }
            for (int j = i + 1; j <= currentPos && numEdges != 0; j++) {
                int bandwidth = j - i;
                if (adjList[currentPermutation[i] - 1].contains(currentPermutation[j] - 1)){
                    numEdges--;
                }
                if (bandwidth > minMaxBandwidth) {
                    return false;
                }
            }
            processed[currentPermutation[i] - 1] = true;
        }
        return true;
    }

    static void backTrack (int[] currentPermutation, Info solution, LinkedList[] adjList, int currentPos, int lowerBound) {
        if (solution.getMinMaxBandwidth() == lowerBound) {
            return;
        }
        if (isASolution(currentPermutation, currentPos)) {
            if (currentPermutation[0] < currentPermutation[currentPermutation.length - 1] && isPartialSolution(currentPermutation, solution.getMinMaxBandwidth(), adjList, currentPos)) {
                int temp = processSolution(currentPermutation, solution.getMinMaxBandwidth(), adjList, currentPos);
                if (temp < solution.getMinMaxBandwidth()) {
                    solution.setMinMaxBandwidth(temp);
                    solution.setSolution(currentPermutation.clone());
                }
            }
        } else {
            currentPos++;
            int[] candidates = constructCandidates(currentPermutation, currentPos);
            for (int i = 0; i < candidates.length; i++) {
                currentPermutation[currentPos] = candidates[i];
                if (currentPos < lowerBound) {
                    backTrack(currentPermutation, solution, adjList, currentPos, lowerBound);
                } else if (isPartialSolution(currentPermutation, solution.getMinMaxBandwidth(), adjList, currentPos)) {
                    backTrack(currentPermutation, solution, adjList, currentPos, lowerBound);
                }
            }
        }
    }
}

class Info {
    private int minMaxBandwidth;
    private int[] solution;
    Info (int i){
        minMaxBandwidth = i;
        solution = new int[i];
    }
    public int[] getSolution() {
        return solution;
    }
    public void setSolution(int[] solution) {
        this.solution = solution;
    }
    public int getMinMaxBandwidth() {
        return minMaxBandwidth;
    }
    public void setMinMaxBandwidth(int minMaxBandwidth) {
        this.minMaxBandwidth = minMaxBandwidth;
    }
}
