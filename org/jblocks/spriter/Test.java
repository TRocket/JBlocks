package org.jblocks.spriter;

public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        SCML scml = SCMLReader.readSCMLFile(Test.class.getResourceAsStream("sample.SCML"), "C:\\Users\\Thomas\\Downloads\\BetaFileFormatRelease\\example hero\\");

        for (Character s : scml.getCharacters()) {
            System.out.println("character name: " + s.getName());
            for (Animation a : s.getAnimations()) {
                System.out.println("animation name: " + a.getName());
                for (KeyFrame f : a.getFrames()) {
                    System.out.println("frame name: " + f.getName() + " duration: " + f.getDuration());
                }
            }
        }
        System.out.println();
        System.out.println();
        System.out.println("Frames:");
        System.out.println();
        System.out.println();
        for (Frame f : scml.getFrames()) {
            System.out.println("Frame name: " + f.getName());
            for (Sprite s : f.getSprites()) {
                System.out.println("	Angle " + s.getAngle());
                System.out.println("	Color " + s.getColor());
                System.out.println("	Height " + s.getHeight());
                System.out.println("	Image " + s.getImage());
                System.out.println("	Opacity " + s.getOpacity());
                System.out.println("	Width " + s.getWidth());
                System.out.println("	X " + s.getX());
                System.out.println("	Y " + s.getY());
                System.out.println();
            }
        }
    }
}
