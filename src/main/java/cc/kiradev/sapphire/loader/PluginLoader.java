package cc.kiradev.sapphire.loader;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PluginLoader {
    public void loadPlugin(InputStream inputStream, String Solar) {
        byte[] pluginBytes = loadJarToMemory(Solar);

        loadPluginFromMemory(pluginBytes);
    }

    private byte[] loadJarToMemory(String Solar) {
        byte[] buffer = null;
        try (FileInputStream fis = new FileInputStream(Solar)) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }
            buffer = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public void loadPluginFromMemory(byte[] pluginBytes) {

        try {
            ClassLoader classLoader = new ClassLoader() {
                @Override
                public Class<?> findClass(String name) throws ClassNotFoundException {
                    return defineClass(null, pluginBytes, 0, pluginBytes.length);
                }
            };

            Class<?> pluginClass = classLoader.loadClass("cc.kiradev.sapphire.plugin.Sapphire");

            Method mainMethod = pluginClass.getMethod("sapphire", String[].class);
            mainMethod.invoke(null, (Object) new String[]{});
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}