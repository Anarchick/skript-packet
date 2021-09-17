package fr.anarchick.skriptpacket.util;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.config.Config;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.Option;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.DefaultExpression;
import ch.njol.skript.lang.ExpressionInfo;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.SyntaxElementInfo;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.log.HandlerList;
import ch.njol.skript.log.LogHandler;
import ch.njol.skript.log.ParseLogHandler;
import ch.njol.skript.log.RetainingLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.btk5h.skriptmirror.SkriptMirror;
import com.btk5h.skriptmirror.skript.custom.event.ExprReplacedEventValue;
import org.bukkit.event.Event;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Original from skript-reflect
 * Works with Skript 2.6+
 * ( https://github.com/TPGamesNL/skript-reflect/blob/2.x/src/main/java/com/btk5h/skriptmirror/util/SkriptReflection.java )
 */

@SuppressWarnings("unchecked")
public class SkriptReflection {

  private static Field PATTERNS;
  private static Field PARAMETERS;
  private static Field HANDLERS;
  private static Field CURRENT_OPTIONS;
  private static Field LOCAL_VARIABLES;
  private static Field NODES;
  private static Field VARIABLES_MAP_HASHMAP;
  private static Field VARIABLES_MAP_TREEMAP;
  private static Constructor<?> VARIABLES_MAP;
  private static Field DEFAULT_EXPRESSION;
  private static Field PARSED_VALUE;
  private static Method PARSE_I;
  private static Field EXPRESSIONS;
  private static Field CURRENT_SCRIPT;
  private static Field HAS_DELAY_BEFORE;

  static {
    Field _FIELD;
    Method _METHOD;
    Constructor<?> _CONSTRUCTOR;

    try {
      _FIELD = SyntaxElementInfo.class.getDeclaredField("patterns");
      _FIELD.setAccessible(true);
      PATTERNS = _FIELD;
    } catch (NoSuchFieldException e) {
      warning("Skript's pattern info field could not be resolved. " +
          "Custom syntax will not work.");
    }

    try {
      //noinspection JavaReflectionMemberAccess
      _FIELD = Function.class.getDeclaredField("parameters");
      _FIELD.setAccessible(true);
      PARAMETERS = _FIELD;
    } catch (NoSuchFieldException ignored) { }

    try {
      //noinspection JavaReflectionMemberAccess
      _FIELD = SkriptLogger.class.getDeclaredField("handlers");
      _FIELD.setAccessible(true);
      HANDLERS = _FIELD;
    } catch (NoSuchFieldException ignored) { }

    try {
      //noinspection JavaReflectionMemberAccess
      _FIELD = ScriptLoader.class.getDeclaredField("currentOptions");
      _FIELD.setAccessible(true);
      CURRENT_OPTIONS = _FIELD;
    } catch (NoSuchFieldException ignored) { }

    try {
      _FIELD = Variables.class.getDeclaredField("localVariables");
      _FIELD.setAccessible(true);
      LOCAL_VARIABLES = _FIELD;
    } catch (NoSuchFieldException e) {
      warning("Skript's local variables field could not be resolved.");
    }

    try {
      _FIELD = SectionNode.class.getDeclaredField("nodes");
      _FIELD.setAccessible(true);
      NODES = _FIELD;
    } catch (NoSuchFieldException e) {
      warning("Skript's nodes field could not be resolved, therefore sections won't work.");
    }

    try {
      Class<?> variablesMap = Class.forName("ch.njol.skript.variables.VariablesMap");

      try {
        _FIELD = variablesMap.getDeclaredField("hashMap");
        _FIELD.setAccessible(true);
        VARIABLES_MAP_HASHMAP = _FIELD;
      } catch (NoSuchFieldException e) {
        warning("Skript's hash map field could not be resolved.");
      }

      try {
        _FIELD = variablesMap.getDeclaredField("treeMap");
        _FIELD.setAccessible(true);
        VARIABLES_MAP_TREEMAP = _FIELD;
      } catch (NoSuchFieldException e) {
        warning("Skript's tree map field could not be resolved.");
      }

      try {
        _CONSTRUCTOR = variablesMap.getDeclaredConstructor();
        _CONSTRUCTOR.setAccessible(true);
        VARIABLES_MAP = _CONSTRUCTOR;
      } catch (NoSuchMethodException e) {
        warning("Skript's variables map constructors could not be resolved.");
      }
    } catch (ClassNotFoundException e) {
      warning("Skript's variables map class could not be resolved.");
    }

    try {
      _FIELD = ClassInfo.class.getDeclaredField("defaultExpression");
      _FIELD.setAccessible(true);
      DEFAULT_EXPRESSION = _FIELD;
    } catch (NoSuchFieldException e) {
      warning("Skript's default expression field could not be resolved, " +
        "therefore event-values won't work in custom events");
    }

    try {
      _FIELD = Option.class.getDeclaredField("parsedValue");
      _FIELD.setAccessible(true);
      PARSED_VALUE = _FIELD;
    } catch (NoSuchFieldException e) {
      warning("Skript's parsed value field could not be resolved, " +
        "therefore and/or warnings won't be suppressed");
    }

    try {
      _METHOD = SkriptParser.class.getDeclaredMethod("parse_i", String.class, int.class, int.class);
      _METHOD.setAccessible(true);
      PARSE_I = _METHOD;
    } catch (NoSuchMethodException e) {
      warning("Skript's parse_i method could not be resolved, therefore prioritized loading won't work.");
    }

    try {
      _FIELD = Skript.class.getDeclaredField("expressions");
      _FIELD.setAccessible(true);
      EXPRESSIONS = _FIELD;
    } catch (NoSuchFieldException e) {
      warning("Skript's expressions field could not be resolved, " +
        "therefore you might get syntax conflict problems");
    }

    try {
      //noinspection JavaReflectionMemberAccess
      _FIELD = ScriptLoader.class.getDeclaredField("currentScript");
      _FIELD.setAccessible(true);
      CURRENT_SCRIPT = _FIELD;
    } catch (NoSuchFieldException ignored) { }

    try {
      //noinspection JavaReflectionMemberAccess
      _FIELD = ScriptLoader.class.getDeclaredField("hasDelayBefore");
      _FIELD.setAccessible(true);
      HAS_DELAY_BEFORE = _FIELD;
    } catch (NoSuchFieldException ignored) { }
  }

  private static void warning(String message) {
    SkriptMirror.getInstance().getLogger().warning(message);
  }


  public static void setPatterns(SyntaxElementInfo<?> info, String[] patterns) {
    try {
      PATTERNS.set(info, patterns);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public static Parameter<?>[] getParameters(Function<?> function) {
    try {
      if (PARAMETERS != null) {
        return (Parameter<?>[]) PARAMETERS.get(function);
      } else {
        return function.getParameters();
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static HandlerList getLogHandlers() {
    try {
      if (HANDLERS != null) {
        return (HandlerList) HANDLERS.get(null);
      } else {
        return ParserInstance.get().getHandlers();
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 1. Stops the given log handler.
   * 2. Stops every {@link ParseLogHandler} until a non-{@link ParseLogHandler} has been reached.
   * 3. Prints the log stored in the given log handler.
   */
  public static void printLog(RetainingLogHandler logger) {
    logger.stop();
    HandlerList handler = getLogHandlers();

    Iterator<LogHandler> handlers = handler.iterator();
    LogHandler nextHandler;
    List<LogHandler> parseLogs = new ArrayList<>();

    while (handlers.hasNext()) {
      nextHandler = handlers.next();

      if (!(nextHandler instanceof ParseLogHandler)) {
        break;
      }
      parseLogs.add(nextHandler);
    }

    parseLogs.forEach(LogHandler::stop);
    SkriptLogger.logAll(logger.getLog());
  }

  /**
   * @return a {@link Map} of the options currently being loaded by {@link ScriptLoader}.
   */
  public static Map<String, String> getCurrentOptions() {
    try {
      if (CURRENT_OPTIONS != null) {
        return (Map<String, String>) CURRENT_OPTIONS.get(null);
      } else {
        return ParserInstance.get().getCurrentOptions();
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sets the local variables of an {@link Event} to the given local variables.
   */

  public static void putLocals(Object originalVariablesMap, Event to) {
    if (originalVariablesMap == null) {
      removeLocals(to);
      return;
    }

    try {
      Map<Event, Object> localVariables = (Map<Event, Object>) LOCAL_VARIABLES.get(null);

      localVariables.put(to, originalVariablesMap);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Removes and returns the local variables from the given {@link Event}.
   */
  public static Object removeLocals(Event event) {
    try {
      Map<Event, Object> localVariables = (Map<Event, Object>) LOCAL_VARIABLES.get(null);
      return localVariables.remove(event);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves the local variables from an {@link Event}.
   * @param event The {@link Event} to get the local variables from.
   * @return The local variables of the given {@link Event}.
   */
  public static Object getLocals(Event event) {
    try {
      Map<Event, Object> localVariables = (Map<Event, Object>) LOCAL_VARIABLES.get(null);
      return localVariables.get(event);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Copies the VariablesMap contained in the given {@link Object}.
   * @param locals The local variables to copy.
   * @return The copied local variables.
   */
  public static Object copyLocals(Object locals) {
    if (locals == null)
      return null;

    try {
      Object copiedLocals = VARIABLES_MAP.newInstance();

      ((Map<String, Object>) VARIABLES_MAP_HASHMAP.get(copiedLocals))
        .putAll((Map<String, Object>) VARIABLES_MAP_HASHMAP.get(locals));
      ((Map<String, Object>) VARIABLES_MAP_TREEMAP.get(copiedLocals))
        .putAll((Map<String, Object>) VARIABLES_MAP_TREEMAP.get(locals));
      return copiedLocals;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException();
    }
  }

  /**
   * Retrieves the {@link Node}s of a {@link SectionNode}.
   * @param sectionNode The {@link SectionNode} to get the nodes from.
   * @return The {@link Node}s of the given {@link SectionNode}
   */
  public static ArrayList<Node> getNodes(SectionNode sectionNode) {
    try {
      return (ArrayList<Node>) NODES.get(sectionNode);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Replaces the event-values of a list of {@link ClassInfo}s with
   * {@link ExprReplacedEventValue}'s to make them work in custom events.
   *
   * @param classInfoList A list of {@link ClassInfo}s to replace
   */
  public static void replaceEventValues(List<ClassInfo<?>> classInfoList) {
    if (DEFAULT_EXPRESSION == null)
      return;

    try {
      List<ClassInfo<?>> replaceExtraList = new ArrayList<>();
      for (ClassInfo<?> classInfo : classInfoList) {
        DefaultExpression<?> defaultExpression = classInfo.getDefaultExpression();
        if (defaultExpression instanceof EventValueExpression && !(defaultExpression instanceof ExprReplacedEventValue)) {
          DEFAULT_EXPRESSION.set(classInfo,
            new ExprReplacedEventValue<>((EventValueExpression<?>) defaultExpression));

          replaceExtraList.add(classInfo);
        }
      }

      replaceExtraList.forEach(SkriptReflection::replaceExtra);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Replaces {@link ClassInfo}s related to the given {@link ClassInfo}.
   */
  public static void replaceExtra(ClassInfo<?> classInfo) {
    List<ClassInfo<?>> classInfoList = Classes.getClassInfos().stream()
      .filter(loopedClassInfo -> !(loopedClassInfo.getDefaultExpression() instanceof ExprReplacedEventValue))
      .filter(loopedClassInfo -> classInfo.getC().isAssignableFrom(loopedClassInfo.getC())
        || loopedClassInfo.getC().isAssignableFrom(classInfo.getC()))
      .collect(Collectors.toList());
    replaceEventValues(classInfoList);
  }

  /**
   * Disable Skript's missing and / or warnings.
   */
  public static void disableAndOrWarnings() {
    Option<Boolean> option = SkriptConfig.disableMissingAndOrWarnings;
    if (!option.value()) {
      try {
        PARSED_VALUE.set(option, true);
      } catch (IllegalAccessException e) {
        throw new RuntimeException();
      }
    }
  }

  /**
   * Executes {@link SkriptParser}'s {@code parse_i} method with the given arguments.
   */
  public static SkriptParser.ParseResult parse_i(SkriptParser skriptParser, String pattern, int i, int j) {
    if (PARSE_I == null)
      return null;

    try {
      return (SkriptParser.ParseResult) PARSE_I.invoke(skriptParser, pattern, i, j);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * {@return} a list of all of Skript's registered {@link ch.njol.skript.lang.Expression}s.
   */
  public static List<ExpressionInfo<?, ?>> getExpressions() {
    try {
      return (List<ExpressionInfo<?, ?>>) EXPRESSIONS.get(null);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static Config getCurrentScript() {
    try {
      if (CURRENT_SCRIPT != null) {
        return (Config) CURRENT_SCRIPT.get(null);
      } else {
        return ParserInstance.get().getCurrentScript();
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static void setCurrentScript(Config currentScript) {
    try {
      if (CURRENT_SCRIPT != null) {
        CURRENT_SCRIPT.set(null, currentScript);
      } else {
        ParserInstance.get().setCurrentScript(currentScript);
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static Kleenean getHasDelayBefore() {
    try {
      if (HAS_DELAY_BEFORE != null) {
        return (Kleenean) HAS_DELAY_BEFORE.get(null);
      } else {
        return ParserInstance.get().getHasDelayBefore();
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static void setHasDelayBefore(Kleenean hasDelayBefore) {
    try {
      if (HAS_DELAY_BEFORE != null) {
        HAS_DELAY_BEFORE.set(null, hasDelayBefore);
      } else {
        ParserInstance.get().setHasDelayBefore(hasDelayBefore);
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

}
