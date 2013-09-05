package models

import collection.mutable.Set

class Library(id: String) {
    var name = id;
    val widgets = Set[String]();
    val plugins = Set[String]();
}