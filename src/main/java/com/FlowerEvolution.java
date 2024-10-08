package com;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Flower {
  private int sizeOfCenter;
  private int[] centerColor = new int[3];
  private int[] petalColor = new int[3];
  private int numberOfPetals;
  private double fitness;

  public Flower(int sizeOfCenter, int[] centerColor, int[] petalColor, int numberOfPetals) {
    this.sizeOfCenter = sizeOfCenter;
    this.centerColor = centerColor;
    this.petalColor = petalColor;
    this.numberOfPetals = numberOfPetals;
    this.fitness = 0.0;
  }

  public static Flower randomFlower() {
    Random rand = new Random();
    int sizeOfCenter = rand.nextInt(10) + 10; // Random size between 10-19
    int[] centerColor = { rand.nextInt(256), rand.nextInt(256), rand.nextInt(256) };
    int[] petalColor = { rand.nextInt(256), rand.nextInt(256), rand.nextInt(256) };
    int numberOfPetals = rand.nextInt(8) + 3; // 3-10 petals
    return new Flower(sizeOfCenter, centerColor, petalColor, numberOfPetals);
  }

  public Circle drawFlower(Pane pane) {
    // Draw the center of the flower
    Circle center = new Circle(sizeOfCenter);
    center.setFill(Color.rgb(centerColor[0], centerColor[1], centerColor[2]));
    center.setTranslateX(new Random().nextInt(400));
    center.setTranslateY(new Random().nextInt(400));
    pane.getChildren().add(center);
    return center;
  }

  public void setFitness(double fitness) {
    this.fitness = fitness;
  }

  public double getFitness() {
    return fitness;
  }

  public void mutate(double mutationRate) {
    Random rand = new Random();
    if (rand.nextDouble() < mutationRate) {
      this.sizeOfCenter = rand.nextInt(10) + 10;
    }
    for (int i = 0; i < 3; i++) {
      if (rand.nextDouble() < mutationRate) {
        this.centerColor[i] = rand.nextInt(256);
      }
      if (rand.nextDouble() < mutationRate) {
        this.petalColor[i] = rand.nextInt(256);
      }
    }
    if (rand.nextDouble() < mutationRate) {
      this.numberOfPetals = rand.nextInt(8) + 3;
    }
  }

  public static Flower crossover(Flower parent1, Flower parent2) {
    Random rand = new Random();
    int sizeOfCenter = rand.nextBoolean() ? parent1.sizeOfCenter : parent2.sizeOfCenter;
    int[] centerColor = {
        rand.nextBoolean() ? parent1.centerColor[0] : parent2.centerColor[0],
        rand.nextBoolean() ? parent1.centerColor[1] : parent2.centerColor[1],
        rand.nextBoolean() ? parent1.centerColor[2] : parent2.centerColor[2],
    };
    int[] petalColor = {
        rand.nextBoolean() ? parent1.petalColor[0] : parent2.petalColor[0],
        rand.nextBoolean() ? parent1.petalColor[1] : parent2.petalColor[1],
        rand.nextBoolean() ? parent1.petalColor[2] : parent2.petalColor[2],
    };
    int numberOfPetals = rand.nextBoolean() ? parent1.numberOfPetals : parent2.numberOfPetals;
    return new Flower(sizeOfCenter, centerColor, petalColor, numberOfPetals);
  }
}

class Population {
  private List<Flower> flowers;
  private double mutationRate;

  public Population(int size, double mutationRate) {
    this.flowers = new ArrayList<>();
    this.mutationRate = mutationRate;
    for (int i = 0; i < size; i++) {
      flowers.add(Flower.randomFlower());
    }
  }

  public void evolve() {
    List<Flower> nextGen = new ArrayList<>();
    for (int i = 0; i < flowers.size(); i++) {
      Flower parent1 = selectFlower();
      Flower parent2 = selectFlower();
      Flower child = Flower.crossover(parent1, parent2);
      child.mutate(mutationRate);
      nextGen.add(child);
    }
    flowers = nextGen;
  }

  private Flower selectFlower() {
    Random rand = new Random();
    return flowers.get(rand.nextInt(flowers.size())); // Random selection
  }

  public List<Flower> getFlowers() {
    return flowers;
  }
}

public class FlowerEvolution extends Application {
  private Population population;
  private final double mutationRate = 0.05;

  @Override
  public void start(Stage primaryStage) {
    Pane pane = new Pane();
    Scene scene = new Scene(pane, 600, 600);

    primaryStage.setTitle("Flower Evolution");
    primaryStage.setScene(scene);
    primaryStage.show();

    population = new Population(8, mutationRate);

    // Display the initial population
    for (Flower flower : population.getFlowers()) {
      Circle flowerCircle = flower.drawFlower(pane);
      setupHoverInteraction(flower, flowerCircle);
    }
  }

  private void setupHoverInteraction(Flower flower, Circle circle) {
    // Track when hovering starts and ends
    long[] startHoverTime = { 0 };

    circle.setOnMouseEntered(event -> {
      startHoverTime[0] = System.currentTimeMillis();
    });

    circle.setOnMouseExited(event -> {
      long endHoverTime = System.currentTimeMillis();
      double hoverDuration = (endHoverTime - startHoverTime[0]) / 1000.0; // Hover time in seconds
      flower.setFitness(flower.getFitness() + hoverDuration); // Add hover time to fitness
      System.out.println("Hover duration: " + hoverDuration + " seconds. Fitness: " + flower.getFitness());
    });
  }

  public static void main(String[] args) {
    launch(args);
  }
}
