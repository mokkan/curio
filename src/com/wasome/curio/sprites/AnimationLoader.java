/*
 * Curio - A simple puzzle platformer game.
 * Copyright (C) 2014  Michael Swiger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.wasome.curio.sprites;

import java.io.InputStream;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

public class AnimationLoader extends

    AsynchronousAssetLoader<Animation, AnimationLoader.AnimationParameter>  {
    
    public AnimationLoader(FileHandleResolver resolver) {
        super(resolver);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Array<AssetDescriptor> getDependencies(String fileName,
            FileHandle file, AnimationParameter parameter) {

        Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
        String dir = file.parent().path() + "/";

        try {
            InputStream in = file.read();
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLEventReader reader = inputFactory.createXMLEventReader(in);
            
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                
                if (event.isStartElement()) {
                    StartElement start = event.asStartElement();
                    String name = start.getName().getLocalPart();
                    
                    if (name != "animation") {
                        continue;
                    }

                    Iterator<Attribute> attributes = start.getAttributes();
                    while (attributes.hasNext()) {
                        Attribute attr = attributes.next();
                        
                        if (attr.getName().toString() == "sheet") {
                            String path = dir + attr.getValue().toString();
                            deps.add(new AssetDescriptor(path, Texture.class));
                        }
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        
        return deps;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName,
            FileHandle file, AnimationParameter parameter) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Animation loadSync(AssetManager manager, String fileName,
            FileHandle file, AnimationParameter parameter) {
        
        Animation anim = null;
        String dir = file.parent().path()  + "/";
        
        try {
            InputStream in = file.read();
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLEventReader reader = inputFactory.createXMLEventReader(in);
            
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                
                if (event.isStartElement()) {
                    StartElement start = event.asStartElement();
                    String name = start.getName().getLocalPart();
                    
                    if (name == "animation") {
                        Texture sheet = null;
                        int w = 0;
                        int h = 0;
                        
                        Iterator<Attribute> attributes = start.getAttributes();
                        
                        while (attributes.hasNext()) {
                            Attribute attr = attributes.next();
                            
                            if (attr.getName().toString() == "sheet") {
                                String path = dir + attr.getValue().toString();
                                sheet = manager.get(path, Texture.class);
                            } else if (attr.getName().toString() == "width") {
                                String tmp = attr.getValue().toString();
                                w = Integer.parseInt(tmp);
                            } else if (attr.getName().toString() == "height") {
                                String tmp = attr.getValue().toString();
                                h = Integer.parseInt(tmp);
                            }
                        }
                        
                        anim = new Animation(sheet, w, h, file.path());
                    } else if (name == "frame") {
                        int x = 0;
                        int y = 0;
                        float time = 0;
                        
                        Iterator<Attribute> attributes = start.getAttributes();
                        
                        while (attributes.hasNext()) {
                            Attribute attr = attributes.next();
                            
                            if (attr.getName().toString() == "x") {
                                String tmp = attr.getValue().toString();
                                x = Integer.parseInt(tmp);
                            } else if (attr.getName().toString() == "y") {
                                String tmp = attr.getValue().toString();
                                y = Integer.parseInt(tmp);
                            } else if (attr.getName().toString() == "time") {
                                String tmp = attr.getValue().toString();
                                time = Integer.parseInt(tmp);
                            }
                        }
                        
                        if (anim != null) {
                            anim.addFrame(x, y, time / 1000);
                        }
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        
        return anim;
    }
    
    static public class AnimationParameter extends
        AssetLoaderParameters<Animation> {
        
    }
}
