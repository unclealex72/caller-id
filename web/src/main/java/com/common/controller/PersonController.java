package com.common.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.common.model.PersonForm;

@RequestMapping("/person")
@Controller
/** Displaying CRUD functionality. **/
public class PersonController {

  Map<Integer, PersonForm> personList = new HashMap<Integer, PersonForm>();
  static int id = 0;

  /**
   * Save a user and return back to display all users
   */
  @RequestMapping(method = RequestMethod.POST)
  public ModelAndView save(
      PersonForm form,
      BindingResult errors,
      HttpServletRequest request,
      HttpServletResponse response) {
    if (form.getId() == -1) {
      id++;
      form.setId(id);
      personList.put(id, form);
    }
    else {
      personList.put(form.getId(), form);
    }
    ;
    ModelMap map = new ModelMap();
    Iterator iter = personList.keySet().iterator();
    List<PersonForm> newMap = new ArrayList<PersonForm>();
    while (iter.hasNext()) {
      Object key = iter.next();
      if (key != null)
        newMap.add(personList.get(key));
    }
    map.put("persons", newMap);
    return new ModelAndView("show", map);
  }

  @RequestMapping(value = "/newuser", method = RequestMethod.GET)
  public ModelAndView newUser() {
    PersonForm form = new PersonForm();
    ModelMap map = new ModelMap();
    map.put("person", form);
    return new ModelAndView("user", map);
  }

  /**
   * 
   * Display all users
   */
  @RequestMapping(method = RequestMethod.GET)
  public String displayAll(HttpServletRequest request) {
    ModelMap map = new ModelMap();
    Iterator iter = personList.keySet().iterator();
    List<PersonForm> newMap = new ArrayList<PersonForm>();
    while (iter.hasNext()) {
      Object key = iter.next();
      if (key != null)
        newMap.add(personList.get(key));
    }
    map.put("persons", newMap);
    request.setAttribute("persons", newMap);
    return "show";
  }

  /** Delete a user and return back to all list **/
  @RequestMapping(value = "/delete/{id}")
  public String delete(@PathVariable String id, HttpServletRequest request) {
    personList.remove(Integer.parseInt(id));
    Iterator iter = personList.keySet().iterator();
    List<PersonForm> newMap = new ArrayList<PersonForm>();
    while (iter.hasNext()) {
      Object key = iter.next();
      if (key != null)
        newMap.add(personList.get(key));
    }
    request.setAttribute("persons", newMap);
    return "show";
  }

  /** Update a user and return back to all list **/
  @RequestMapping(value = "/update/{id}")
  public ModelAndView update(@PathVariable String id) {
    ModelMap map = new ModelMap();
    map.put("person", personList.get(Integer.parseInt(id)));
    return new ModelAndView("user", map);

  }

}