package br.edu.ufrgs.inf.bpm.application;

import java.util.ArrayList;
import java.util.List;

public class SimilaritySearcher {

    public static void main(String[] args) {
        SimilaritySearcher similaritySearcher = new SimilaritySearcher(4);
        while(similaritySearcher.hasNext()){
            similaritySearcher.next().stream().map(result -> String.format("%.2f", result) + " ").forEach(System.out::print);
            System.out.println();
        }
    }

    private double maxValue;
    private double variation;

    private List<Double> coeficientList;

    public SimilaritySearcher(int amountCoeficients){
        this(amountCoeficients, 1, 0.1);
    }

    public SimilaritySearcher(int amountCoeficients, double maxValue, double variation){
        this.maxValue = maxValue;
        this.variation = variation;

        coeficientList = new ArrayList<>();
        for(int index = 0; index < amountCoeficients; index++){
            coeficientList.add(0.0);
        }
    }

    public boolean hasNext(){
        return coeficientList.stream().anyMatch(e -> Math.abs(e-maxValue) > 0.0001);
    }

    public List<Double> next(){
        int index = coeficientList.size() - 1;
        while(index >= 0) {
            double newCoeficient = coeficientList.get(index) + variation;
            if(newCoeficient > maxValue) {
                coeficientList.set(index, 0.0);
                index--;
            } else {
                coeficientList.set(index, newCoeficient);
                return coeficientList;
            }
        }
        return null;
    }

}
