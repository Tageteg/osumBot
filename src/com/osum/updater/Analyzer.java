package com.osum.updater;

import com.osum.updater.deobfuscator.Deobfuscator;
import com.osum.updater.deobfuscator.impl.MethodDeobfuscator;
import com.osum.updater.examine.multiplier.MultiplierExaminer;
import com.osum.updater.examine.multiplier.Multipliers;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.Identity;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.impl.ClientAnalyzer;
import com.osum.updater.impl.tree1.input.CanvasAnalyzer;
import com.osum.updater.impl.tree1.input.KeyboardAnalyzer;
import com.osum.updater.impl.tree1.input.MouseAnalyzer;
import com.osum.updater.impl.tree1.misc.*;
import com.osum.updater.impl.tree2.animation.AnimationAnalyzer;
import com.osum.updater.impl.tree2.entity.*;
import com.osum.updater.impl.tree2.item.ItemAnalyzer;
import com.osum.updater.impl.tree3.model.ModelAnalyzer;
import com.osum.updater.impl.tree4.objects.*;
import com.osum.updater.impl.tree4.world.*;
import com.osum.updater.impl.tree5.component.WidgetAnalyzer;
import com.osum.updater.impl.tree5.component.WidgetNodeAnalyzer;
import com.osum.updater.util.Accessors;
import com.osum.updater.util.XMLWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Analyzer
{

    private Map<String, ClassNode> classes;
    private final Accessors accessors;
    private final ConcurrentLinkedQueue<ClassAnalyzer> analyzers;
    private final List<ClassIdentity> identities;
    private final Deobfuscator[] deobfuscators = new Deobfuscator[]{new MethodDeobfuscator()};

    public Analyzer(final Map<String, ClassNode> classes)
    {
        this.classes = classes;
        this.identities = new ArrayList<ClassIdentity>();
        this.accessors = new Accessors();
        this.analyzers = new ConcurrentLinkedQueue<ClassAnalyzer>();
        this.analyzers.addAll(getAnalyzers());
    }

    public void run()
    {
        //final ClassNode versionNode = getClasses().get("client");
        //System.out.println("\nRs Revision : " + new VersionResolver(versionNode != null ? versionNode : getClasses().get("Client")).resolve());
        System.out.println("\nRunning deobfuscators");
        final long deobTime = System.currentTimeMillis();
        for (final Deobfuscator deobfuscator : deobfuscators)
        {
            this.classes = deobfuscator.visit(this.classes);
        }
        System.out.println("Finished deobfuscating in " + (System.currentTimeMillis() - deobTime) + "ms");
        final long multiplierTime = System.currentTimeMillis();
        System.out.println("\nAnalysing multipliers");
        MultiplierExaminer.findMultipliers(classes);
        Multipliers.decideMultipliers();
        System.out.println("Analysed " + Multipliers.multipliers.size() + " multipliers In " + (System.currentTimeMillis() - multiplierTime) + "ms");
        final long analysisTime = System.currentTimeMillis();
        System.out.println("\nAnalysing classes");
        for (int timeout = 0; timeout < 100; timeout++)
        {
            for (final ClassAnalyzer analyzer : analyzers)
            {
                for (final ClassNode classNode : classes.values())
                {
                    if (analyzer.accessors() == null || (analyzer.accessors() != null && accessors.contains(analyzer.accessors())))
                    {
                        final ClassIdentity classIdentity = analyzer.accept(classNode);
                        if (classIdentity != null)
                        {
                            analyzer.analyse(classIdentity);
                            final List<FieldIdentity> fieldIdentities = analyzer.getIdentities();
                            if (fieldIdentities != null)
                            {
                                classIdentity.addAll(fieldIdentities);
                            }
                            identities.add(classIdentity);
                            analyzers.remove(analyzer);
                            break;
                        }
                    }
                }
            }
            if (timeout == 100)
            {
                System.out.println("\nCouldn't complete all analyzers\n");
                break;
            }
        }
        for (final ClassIdentity identity : getIdentities())
        {
            if (identity.getIndentation() == 0)
            {
                System.out.println();
            }
            System.out.println(addIndentation(identity.getIndentation()) + "^ " + identity.getName() + " = " + identity.getClassName());
            for (final FieldIdentity field : identity.getFieldIdentities())
            {
                if (field.getMultiplier() != -1)
                {
                    System.out.println(addIndentation(identity.getIndentation()) + "-> " + field.getMethodName() + " identified as " + field.getFieldName() + "(" + field.getFieldType() + ") * " + field.getMultiplier());
                } else
                {
                    System.out.println(addIndentation(identity.getIndentation()) + "-> " + field.getMethodName() + " identified as " + field.getFieldName() + "(" + field.getFieldType() + ")");
                }
            }
        }
        System.out.println("\nAnalysis identified " + identities.size() + " classes");
        System.out.println("Analysis identified " + getFieldCount() + " fields");
        System.out.println("Analysis failed to identify " + analyzers.size() + " classes");
        System.out.println("Analysis time " + (System.currentTimeMillis() - analysisTime) + "ms");
        System.out.println("\nDumping hooks to XML");
        new XMLWriter(identities).write();
        System.out.println("Finished dumping hooks");
    }

    public ArrayList<ClassAnalyzer> getAnalyzers()
    {
        final ArrayList<ClassAnalyzer> analyzers = new ArrayList<>();
        //Tree 1
        {
            analyzers.add(new CanvasAnalyzer(this));
            analyzers.add(new MouseAnalyzer(this));
            analyzers.add(new KeyboardAnalyzer(this));
            analyzers.add(new LinkedListNodeAnalyzer(this));
            analyzers.add(new LinkedListAnalyzer(this));
            {
                analyzers.add(new NodeAnalyzer(this));
                analyzers.add(new BufferAnalyzer(this));
                analyzers.add(new CacheableNodeAnalyzer(this));
            }
            analyzers.add(new HashTableAnalyzer(this));
            analyzers.add(new CacheAnalyzer(this));
            analyzers.add(new VarpBitsAnalyzer(this));
            analyzers.add(new FacadeAnalyzer(this));
            {
                analyzers.add(new ItemAnalyzer(this));
            }
            analyzers.add(new NodeDequeAnalyzer(this));
            analyzers.add(new ViewPortAnalyzer(this));
            {
                analyzers.add(new AnimationAnalyzer(this));
            }

        }
        //Tree 2
        {
            analyzers.add(new RenderableAnalyzer(this));
            analyzers.add(new ActorAnalyzer(this));
            analyzers.add(new PlayerAnalyzer(this));
            analyzers.add(new PlayerDefinitionAnalyzer(this));
            analyzers.add(new NPCAnalyzer(this));
            analyzers.add(new NPCDefinitionAnalyzer(this));
            analyzers.add(new ProjectileAnalyzer(this));
        }
        //Tree 3
        {
            analyzers.add(new ModelAnalyzer(this));
        }
        //Tree 4
        {
            {
                analyzers.add(new TileAnalyzer(this));
                analyzers.add(new SceneAnalyzer(this));
                analyzers.add(new RegionAnalyzer(this));
                analyzers.add(new GroundItemAnalyzer(this));
            }
            {
                analyzers.add(new GameObjectAnalyzer(this));
                analyzers.add(new GameObjectDefinitionAnalyzer(this));
                analyzers.add(new InteractableObjectAnalyzer(this));
                {
                    analyzers.add(new WallAnalyzer(this));
                    analyzers.add(new FloorDecorationAnalyzer(this));
                    analyzers.add(new WallDecorationAnalyzer(this));
                    analyzers.add(new GroundLayerAnalyzer(this));
                }
            }
        }
        // Tree 5
        {
            analyzers.add(new WidgetAnalyzer(this));
            analyzers.add(new WidgetNodeAnalyzer(this));
        }
        {
            analyzers.add(new ClientAnalyzer(this));
        }
        return analyzers;
    }

    public List<ClassIdentity> getIdentities()
    {
        return this.identities;
    }

    public Map<String, ClassNode> getClasses()
    {
        return this.classes;
    }

    public Accessors getAccessors()
    {
        return this.accessors;
    }

    public int getFieldCount()
    {
        int count = 0;
        for (final Identity identity : identities)
        {
            if (identity instanceof ClassIdentity)
            {
                count += ((ClassIdentity) identity).getFieldIdentities().size();
            }
        }
        return count;
    }

    public String addIndentation(final int indentation)
    {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indentation; i++)
        {
            builder.append("-");
        }
        return builder.toString();
    }
}
