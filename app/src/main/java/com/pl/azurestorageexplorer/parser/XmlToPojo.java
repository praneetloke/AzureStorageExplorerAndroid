package com.pl.azurestorageexplorer.parser;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Praneet Loke on 7/3/2016.
 */
public class XmlToPojo {
    public static <T> T parseItem(Class<T> classOfTypeT, InputStream is, Xml.Encoding encoding) {
        Object item = null;
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();

            parser.setInput(is, encoding.name());

            int eventType = parser.getEventType();

            String text = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase(classOfTypeT.getSimpleName())) {
                            // create a new instance of parent type
                            item = Class.forName(classOfTypeT.getName()).newInstance();
                        } else if (item != null) {
                            try {
                                Field field = item.getClass().getDeclaredField(tagName);
                                //if this tag represents a field that is not a primitive type
                                if (!field.getType().isPrimitive() && field.getType() != String.class) {
                                    //recursively set its values
                                    Method method = null;
                                    try {
                                        method = item.getClass().getMethod(String.format("set%s", tagName), field.getType());
                                        method.invoke(item, GetItemValue(field.getType(), parser));
                                    } catch (NoSuchMethodException e) {
                                        //ignore
                                    }
                                }
                            } catch (NoSuchFieldException e) {
                                //we couldn't find the field in the model provided, so ignore and continue
                            }
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (item != null) {
                            Method method = null;
                            try {
                                method = item.getClass().getMethod(String.format("set%s", tagName), String.class);
                                method.invoke(item, text);
                            } catch (NoSuchMethodException e) {
                                //ignore
                            }
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return (T) item;
    }

    public static <T> List<T> parseList(Class<T> classOfTypeT, InputStream is, Xml.Encoding encoding) {
        List<T> items = new ArrayList<>();
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();

            parser.setInput(is, encoding.name());

            int eventType = parser.getEventType();
            Object item = null;
            String text = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase(classOfTypeT.getSimpleName())) {
                            // create a new instance of parent type
                            item = Class.forName(classOfTypeT.getName()).newInstance();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase(classOfTypeT.getSimpleName())) {
                            // add item object to list
                            items.add((T) item);
                        } else if (item != null) {
                            Method method = null;
                            try {
                                method = item.getClass().getMethod(String.format("set%s", tagName), String.class);
                                method.invoke(item, text);
                            } catch (NoSuchMethodException e) {
                                //ignore
                            }
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return items;
    }

    private static <T> T GetItemValue(Class<T> classOfTypeT, XmlPullParser parser)
            throws XmlPullParserException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, IOException {
        Object item = null;
        int eventType = parser.getEventType();
        String text = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tagName.equalsIgnoreCase(classOfTypeT.getSimpleName())) {
                        // create a new instance of parent type
                        item = Class.forName(classOfTypeT.getName()).newInstance();
                    } else if (item != null) {
                        try {
                            Field field = item.getClass().getDeclaredField(tagName);
                            //if this tag represents a field that is not a primitive type
                            if (!field.getType().isPrimitive() && field.getType() != String.class) {
                                //recursively set its values
                                Method method = null;
                                try {
                                    method = item.getClass().getMethod(String.format("set%s", tagName), field.getType());
                                    method.invoke(item, GetItemValue(field.getType(), parser));
                                } catch (NoSuchMethodException e) {
                                    //ignore
                                }
                            }
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;

                case XmlPullParser.END_TAG:
                    if (item != null) {
                        Method method = null;
                        try {
                            method = item.getClass().getMethod(String.format("set%s", tagName), String.class);
                            method.invoke(item, text);
                        } catch (NoSuchMethodException e) {
                            //ignore
                        }
                    }
                    break;

                default:
                    break;
            }
            eventType = parser.next();
        }

        return (T) item;
    }
}
