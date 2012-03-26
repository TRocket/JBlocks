package org.jblocks.blockloader;

/**
 * a simple class which stores information about a dependency
 * @author TRocket
 *
 */
public class Dependency {

    private String dependency;
    private String name;

    /**
     * @param dependency the class of the dependency
     * @param name the dependency's friendly name
     */
    public Dependency(String dependency, String name) {
        this.dependency = dependency;
        this.name = name;
    }

    /**
     * @return the dependency
     */
    public String getDependency() {
        return dependency;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * check if this dependency is on the classpath and available
     * @return <b>true</b> if the class is available
     * <br>
     * <b>false</b>if the class is not available
     */
    public boolean isAvailable() {
        try {
            Class.forName(dependency);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
