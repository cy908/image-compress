package com.xyz.app.image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xyz.app.config.Config;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Slf4j
@Component
public class Compress {

	@Autowired
	private Config config;

	public void run() {
		String src = config.getSrc();
		String out = config.getOut();
		Long size = config.getSize();
		if (src == null || "".equals(src.trim())) {
			log.error("[src]参数错误！");
			return;
		}
		if (out == null || "".equals(out.trim())) {
			log.error("[out]参数错误！");
			return;
		}
		if (size == null || size <= 0) {
			log.error("[size]参数错误！");
			return;
		}
		File file = new File(src);
		if (!file.exists()) {
			log.error(MessageFormat.format("[{0}]不存在！", src));
			return;
		}
		file = new File(out);
		if (!file.exists()) {
			if (file.isDirectory()) {
				log.error(MessageFormat.format("[{0}]不是文件！", out));
				return;
			}
			if (!file.getParentFile().exists()) {
				if (!file.getParentFile().mkdirs()) {
					log.error(MessageFormat.format("[{0}]创建失败！", file.getParentFile().getAbsolutePath()));
					return;
				}
			}
		} else {
			if (file.isFile()) {
				log.error(MessageFormat.format("[{0}]已经存在！", out));
				return;
			}
		}
		read(src, out, size);
	}

	private void read(String src, String out, long size) {
		File file = new File(src);
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				read(f.getAbsolutePath(), out, size);
			}
		} else {
			String name = file.getName();
			if (name.matches("(?i).*\\.(?:jpg|jpeg|png)$")) {
				compress(src, out, name, size);
			} else {
				log.warn(MessageFormat.format("[{0}]非图片！", name));
			}
		}
	}

	private void compress(String src, String out, String filename, long size) {
		float sca = 1, qua = 1, kb = 1024;
		long len = new File(src).length();
		String pathname = out + File.separator + filename;
		if (len <= size * kb) {
			try {
				Files.copy(Paths.get(src), Paths.get(pathname));
			} catch (IOException e) {
				log.warn(MessageFormat.format("[{0}]拷贝失败！", src));
			}
			log.info(MessageFormat.format("[{0}]大小[{1}KB]！", src, Math.ceil(len / kb)));
			return;
		}
		try {
			while (len > size * kb) {
				qua -= 0.1;
				Thumbnails.of(src).scale(sca).outputQuality(qua).toFile(pathname);
				len = new File(pathname).length();
				log.info(MessageFormat.format("[{0}]大小[{1}KB]！", src, Math.ceil(len / kb)));
			}
		} catch (IOException e) {
			log.warn(MessageFormat.format("[{0}]压缩失败！", src));
		}
	}

}
