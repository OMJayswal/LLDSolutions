package com.test;

import java.util.*;

/** Core pricing component */
interface PricedPizza {
    double getSubtotal();          // base + toppings
    double getTaxRatePercent();    // effective tax %, after any modifiers
    String getSize();
}

/** Concrete component: bare pizza */
final class BarePizza implements PricedPizza {
    private final int basePrice;
    private final double baseTaxPercent;
    private final String size;

    BarePizza(int basePrice, int taxPercentage, String size) {
        this.basePrice = basePrice;
        this.baseTaxPercent = taxPercentage;
        this.size = size;
    }

    @Override public double getSubtotal() { return basePrice; }
    @Override public double getTaxRatePercent() { return baseTaxPercent; }
    @Override public String getSize() { return size; }
}

/** Abstract decorator */
abstract class ToppingDecorator implements PricedPizza {
    protected final PricedPizza inner;
    protected int servings;

    protected ToppingDecorator(PricedPizza inner, int servings) {
        if (servings <= 0) throw new IllegalArgumentException("servings must be positive");
        this.inner = inner;
        this.servings = servings;
    }

    public void addServings(int more) {
        if (more <= 0) throw new IllegalArgumentException("servings must be positive");
        this.servings += more;
    }

    /** Lowercase canonical name used by the facade + rules */
    public abstract String name();

    /** Cost contributed by this decorator */
    protected abstract double toppingCost();

    /** Default: toppings do not change tax rate; override if they do */
    protected double taxMultiplier() { return 1.0; }

    @Override public double getSubtotal() { return inner.getSubtotal() + toppingCost(); }

    @Override public double getTaxRatePercent() {
        return inner.getTaxRatePercent() * taxMultiplier();
    }

    @Override public String getSize() { return inner.getSize(); }
}

/** Concrete toppings (catalog-driven costs) */

// cheeseburst: first=100, additional=70; one-time +30% tax uplift
final class Cheeseburst extends ToppingDecorator {
    Cheeseburst(PricedPizza inner, int servings) { super(inner, servings); }

    @Override protected double toppingCost() {
        if (servings <= 0) return 0;
        return 100 + Math.max(0, servings - 1) * 70;
    }
    @Override protected double taxMultiplier() { return 1.30; } // one-time uplift
    @Override public String name() { return "cheeseburst"; }
}

final class Corn extends ToppingDecorator {
    Corn(PricedPizza inner, int servings) { super(inner, servings); }
    @Override protected double toppingCost() { return 50.0 * servings; }
    @Override public String name() { return "corn"; }
}
final class Onion extends ToppingDecorator {
    Onion(PricedPizza inner, int servings) { super(inner, servings); }
    @Override protected double toppingCost() { return 30.0 * servings; }
    @Override public String name() { return "onion"; }
}
final class Capsicum extends ToppingDecorator {
    Capsicum(PricedPizza inner, int servings) { super(inner, servings); }
    @Override protected double toppingCost() { return 50.0 * servings; }
    @Override public String name() { return "capsicum"; }
}
final class Pineapple extends ToppingDecorator {
    Pineapple(PricedPizza inner, int servings) { super(inner, servings); }
    @Override protected double toppingCost() { return 60.0 * servings; }
    @Override public String name() { return "pineapple"; }
}
final class Mushroom extends ToppingDecorator {
    Mushroom(PricedPizza inner, int servings) { super(inner, servings); }
    @Override protected double toppingCost() { return 40.0 * servings; }
    @Override public String name() { return "mushroom"; }
}

/** ---- Rule engine (easy to extend) ---- */
interface Rule {
    /**
     * Return true if the topping can be applied.
     * No state must be mutated by rules; the facade applies changes only after all rules pass.
     */
    boolean canApply(PizzaState state, String topping, int servings);
}

final class PizzaState {
    final String size;
    final Map<String, Integer> toppingCounts; // lowercase name -> servings
    PizzaState(String size, Map<String, Integer> toppingCounts) {
        this.size = size;
        this.toppingCounts = Collections.unmodifiableMap(new HashMap<>(toppingCounts));
    }
    boolean has(String name) { return toppingCounts.getOrDefault(name.toLowerCase(Locale.ROOT), 0) > 0; }
}

/** Health constraint: mushroom and cheeseburst are mutually exclusive */
final class MutualExclusionRule implements Rule {
    @Override public boolean canApply(PizzaState state, String topping, int servings) {
        String t = topping.toLowerCase(Locale.ROOT);
        if (t.equals("cheeseburst") && state.has("mushroom")) return false;
        if (t.equals("mushroom") && state.has("cheeseburst")) return false;
        return true;
    }
}

/** ---- Topping factory registry (extensible) ---- */
interface ToppingFactory {
    ToppingDecorator create(PricedPizza inner, int servings);
}
final class ToppingRegistry {
    private final Map<String, ToppingFactory> factories = new HashMap<>();
    ToppingRegistry() {
        register("cheeseburst", Cheeseburst::new);
        register("corn",       Corn::new);
        register("onion",      Onion::new);
        register("capsicum",   Capsicum::new);
        register("pineapple",  Pineapple::new);
        register("mushroom",   Mushroom::new);
    }
    public void register(String name, ToppingFactory factory) {
        factories.put(name.toLowerCase(Locale.ROOT), factory);
    }
    public boolean knows(String name) { return factories.containsKey(name.toLowerCase(Locale.ROOT)); }

    public ToppingDecorator make(String name, PricedPizza inner, int servings) {
        ToppingFactory f = factories.get(name.toLowerCase(Locale.ROOT));
        if (f == null) throw new IllegalArgumentException("Unknown topping: " + name);
        return f.create(inner, servings);
    }
}

/** ---- Facade users interact with ---- */
public class PizzaPricingSystem {
    private PricedPizza root; // decorated chain
    private final Map<String, ToppingDecorator> byName = new HashMap<>(); // one instance per topping
    private final Map<String, Integer> counts = new HashMap<>();
    private final List<Rule> rules = new ArrayList<>();
    private final ToppingRegistry registry = new ToppingRegistry();

    public  PizzaPricingSystem(int basePrice, int taxPercentage, String size) {
        this.root = new BarePizza(basePrice, taxPercentage, size);
        // install default rules; you can add more later without changing core code
        rules.add(new MutualExclusionRule());
    }

    /** Add a rule at runtime (e.g., size caps, promos, combos) */
    public void addRule(Rule rule) { rules.add(rule); }

    public boolean addTopping(String topping, int servingsCount) {
        String key = topping.toLowerCase(Locale.ROOT);
        if (servingsCount <= 0) return false;
        if (!registry.knows(key)) throw new IllegalArgumentException("Unknown topping: " + topping);

        // Evaluate rules against a read-only snapshot
        PizzaState snapshot = new PizzaState(root.getSize(), counts);
        for (Rule r : rules) {
            if (!r.canApply(snapshot, key, servingsCount)) return false; // NO state change
        }

        // Passed all rules → apply mutation.
        if (byName.containsKey(key)) {
            // Increase servings on existing decorator (keeps one-time effects one-time)
            byName.get(key).addServings(servingsCount);
            counts.put(key, counts.get(key) + servingsCount);
        } else {
            // Create and wrap
            ToppingDecorator deco = registry.make(key, root, servingsCount);
            root = deco; // new head of chain
            byName.put(key, deco);
            counts.put(key, servingsCount);
        }
        return true;
    }

    public int getFinalPrice() {
        double subtotal = root.getSubtotal();
        double taxRate = root.getTaxRatePercent(); // already includes any decorator multipliers
        double taxAmount = (taxRate / 100.0) * subtotal;
        double finalPreRound = subtotal + taxAmount;
        return (int) (finalPreRound + 0.5); // round half up (prices are non-negative)
    }

    // --- Convenience for tests / introspection (optional) ---
    Map<String, Integer> getToppingCounts() { return Collections.unmodifiableMap(counts); }
    double getSubtotalForDebug() { return root.getSubtotal(); }
    double getEffectiveTaxPercentForDebug() { return root.getTaxRatePercent(); }

    // ---- Demo / simple tests matching the worked examples ----
    public static void main(String[] args) {
        // Example A
        PizzaPricingSystem p = new PizzaPricingSystem(200, 10, "small");
        System.out.println(p.addTopping("cheeseburst", 1)); // true
        System.out.println(p.addTopping("onion", 2));       // true
        System.out.println(p.getFinalPrice());              // 407

        // Example B
        PizzaPricingSystem q = new PizzaPricingSystem(350, 8, "medium");
        System.out.println(q.addTopping("mushroom", 2));    // true
        System.out.println(q.addTopping("cheeseburst", 1)); // false (mutual exclusion)
        System.out.println(q.getFinalPrice());              // 464
    }
}
