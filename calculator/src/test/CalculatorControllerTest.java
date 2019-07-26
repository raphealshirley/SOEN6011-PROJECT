package test;

import calculator.CalculatorController;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CalculatorControllerTest {
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(CalculatorController.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @org.junit.Test
    public void calculate() {
    }

    @org.junit.Test
    public void sinh() {
    }

    @org.junit.Test
    public void expm1() {
    }

    @org.junit.Test
    public void abs() {
    }

    @org.junit.Test
    public void exp() {
    }
}
