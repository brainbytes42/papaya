
package de.brainbytes.common.xml.bind.annotation.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Fabian Schink
 *
 */
/*
 * As the adapter is used only internally by jaxb, it's as safe as possible with unknown types and
 * reference by plain class (@XmlJavaTypeAdapter(value = WrapperAdapter.class))
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class WrapperAdapter extends XmlAdapter<Object, Object> {

  @XmlElement(name = "item")
  private Collection items = new ArrayList();
  @XmlAttribute
  private Class<?> wrappedType;
  @XmlAttribute
  private Wrappable wrapped;

  @SuppressWarnings("unused")
  private WrapperAdapter() {/* JAXB */ }

  public WrapperAdapter(final Object content) {
    if (Wrappable.COLLECTION.associatedClass.isInstance(content)) {
      items = (Collection) content;
      wrapped = Wrappable.COLLECTION;
    } else if (Wrappable.MAP.associatedClass.isInstance(content)) {
      ((Map) content)
          .forEach((final Object key, final Object value) -> items.add(new MapItem(key, value)));
      wrapped = Wrappable.MAP;
    } else {
      throw new IllegalArgumentException(
          "Can't wrap " + content + " (" + content.getClass() + ").");
    }

    wrappedType = content.getClass();
  }

  /**
   * @return
   */
  Object unwrap() {

    Object unwrapped;
    try {
      unwrapped = wrappedType.newInstance();
    } catch (final Exception e) {
      e.printStackTrace(); // TODO
      unwrapped = wrapped.defaultObject;
    }

    switch (wrapped) {
      case COLLECTION:
        ((Collection) unwrapped).addAll(items);
        break;

      case MAP:
        for (final Object item : items) {
          ((Map) unwrapped).put(((MapItem) item).key, ((MapItem) item).value);
        }
        break;

      default:
        throw new IllegalArgumentException(
            "Unsupported Strategy '" + wrapped + "' for unwrapping.");
    }

    return unwrapped;
  }

  /**
   * @param v
   * @return
   * @throws Exception
   * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
   */
  @Override
  public Object marshal(final Object modelObj) throws Exception {
    if (Wrappable.isWrappable(modelObj)) {
      return new WrapperAdapter(modelObj);
    } else {
      return modelObj;
    }
  }

  /**
   * @param v
   * @return
   * @throws Exception
   * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
   */
  @Override
  public Object unmarshal(final Object xmlObj) throws Exception {
    if (xmlObj instanceof WrapperAdapter) {
      return ((WrapperAdapter) xmlObj).unwrap();
    } else {
      return xmlObj;
    }
  }

  public static Set<Class<?>> getTypesToMarshal() {
    final Set<Class<?>> types = new HashSet<>();

    types.add(WrapperAdapter.class);
    types.add(MapItem.class);

    return types;
  }

  enum Wrappable {
    COLLECTION(Collection.class, new ArrayList<>()),

    MAP(Map.class, new HashMap<>());

    final Class<?> associatedClass;
    final Object defaultObject;

    Wrappable(final Class<?> associatedClass, final Object defaultObject) {
      this.associatedClass = associatedClass;
      this.defaultObject = defaultObject;
    }

    static boolean isWrappable(final Object o) {
      for (final Wrappable wrappable : Wrappable.values()) {
        if (wrappable.associatedClass.isInstance(o)) {
          return true;
        }
      }
      return false;
    }
  }

  static class MapItem {
    @XmlElement
    Object key;
    @XmlElement
    Object value;

    public MapItem() {/* JAXB */}

    MapItem(final Object key, final Object value) {
      this.key = key;
      this.value = value;
    }
  }
}