package nkcs.networking.fnl;

import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

import nkcs.networking.ui.UI_LogIn;

public class Entry {

	public static void main(String arg[]) {
		UI_LogIn login = new UI_LogIn();

		new Listening();

		new Entry();

	}
}
