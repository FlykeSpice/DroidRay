/*******************************************************************************
 * vfeplatform.cpp
 *
 * This module contains *nix platform-specific support code for the VFE.
 *
 * Based on vfe/win/vfeplatform.cpp by Christopher J. Cason
 *
 * ---------------------------------------------------------------------------
 * Persistence of Vision Ray Tracer ('POV-Ray') version 3.7.
 * Copyright 1991-2013 Persistence of Vision Raytracer Pty. Ltd.
 *
 * POV-Ray is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * POV-Ray is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ---------------------------------------------------------------------------
 * POV-Ray is based on the popular DKB raytracer version 2.12.
 * DKBTrace was originally written by David K. Buck.
 * DKBTrace Ver 2.0-2.12 were written by David K. Buck & Aaron A. Collins.
 * ---------------------------------------------------------------------------
 * $File: //depot/povray/smp/vfe/unix/vfeplatform.cpp $
 * $Revision: #18 $
 * $Change: 6132 $
 * $DateTime: 2013/11/25 14:23:41 $
 * $Author: clipka $
 *******************************************************************************/

// must come first
#include "syspovconfig.h"

#include <pthread.h>
#include "vfe.h"

namespace vfePlatform
{
	/////////////////////////////////////////////////////////////////////////
	// return a number that uniquely identifies the calling thread amongst
	// all other running threads in the process (and preferably in the OS).
	POVMS_Sys_Thread_Type GetThreadId ()
	{
		return (POVMS_Sys_Thread_Type) pthread_self();
	}
}
