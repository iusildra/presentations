---
layout: section
transition: fade
---

# Original problem

---
transition: fade
---

## What is a program

<div style="text-align: center" v-click>

```mermaid
flowchart LR
  A@{ shape: lean-r, label: "Input" }
  A --> B["Program"]
  B --> C@{ shape: lean-r, label: "Output" }
```

</div>

<v-click><h2>What we want</h2></v-click>

<div style="text-align: center">
<div v-click>

```mermaid
flowchart LR
  A@{ shape: lean-r, label: "🍎" }
  A --> B["Program"]
  B --> C@{ shape: lean-r, label: "🔴" }
```

</div>
<div v-click>

```mermaid
flowchart LR
  A@{ shape: lean-r, label: "🍏" }
  A --> B["Program"]
  B --> C@{ shape: lean-r, label: "🟢" }
```

</div>
<div v-click>

```mermaid
flowchart LR
  A@{ shape: lean-r, label: "🍎" }
  A --> B["Program"]
  B --> C@{ shape: lean-r, label: "🍎" }
```

</div>
</div>

---
transition: fade
---

## Stateful programs 1

```mermaid {scale: 0.8}
sequenceDiagram
  participant P as Program
  participant A as Thread 1
  participant B as Thread 2
  participant M as Memory
  P->>A: Pay €3k bill 💸
  P->>B: Pay €6k bill 💸
  Note over M: Starting balance: €10k
  A->>M: Read balance
  M-->>A: 
  B->>M: Read balance
  M-->>B: 
  A->>M: New balance: €7k
  B->>M: ⚠️ New balance: €4k ⚠️
```

---
transition: fade
---
## Stateful programs 2

```scala
class BankClient(var mainAccountId: Int, var customRate: Double, var leaningRate: Double) {
  def updateLeaningRate(/* arguments */) = {
    // some very complex logic
    leaningRate = // new value
    // other logic
  }
}
```

Want to change the above code ? You first need to

<v-clicks>

- Understand the whole class
- Understand the whole logic, beyond the method / class
- Think about edge cases (hard by definition)
- Write tests for your changes
- Write tests for those edge cases (usually requires lots of setup beforehand...)

</v-clicks>
