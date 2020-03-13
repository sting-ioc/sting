package sting.doc.examples.fragments;

import sting.Fragment;

@Fragment(
  includes = { UserRepository.class,
               PermissionRepository.class,
               GroupRepository.class } )
public interface SecurityFragment
{
}
