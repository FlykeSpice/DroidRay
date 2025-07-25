/*******************************************************************************
 * metadata.h
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
 * $File: //depot/public/povray/3.x/source/base/image/metadata.h $
 * $Revision: #1 $
 * $Change: 6069 $
 * $DateTime: 2013/11/06 11:59:40 $
 * $Author: chrisc $
 *******************************************************************************/

#ifndef POVRAY_BASE_METADATA_H
#define POVRAY_BASE_METADATA_H

#include <chrono>
#include <ctime>
#include <string>

#include "backend/povray.h"

namespace pov_base
{

/// Generates metadata to write into output images.
class Metadata
{
	public:
		Metadata()
		{
			auto now = std::chrono::system_clock::now();
			std::time_t newt = std::chrono::system_clock::to_time_t(now);

			auto gm = std::gmtime(&newt);

			std::memcpy(&date, gm, sizeof(std::tm));
		}

		virtual ~Metadata(){}

		/**
		 *  Get software string.
		 *  @note   This method should return at most 40 ascii bytes, otherwise it may become truncated by some file formats.
		 *          non-printable characters, line feeds and tabs are not allowed.
		 */
		static string getSoftware() { return "POV-Ray " POV_RAY_VERSION;}

		/* Up to 4 comments, each at most 80 ascii bytes, no line feed, no tab 
		 * if it's longer, it can either fit anyway or get truncated, it's the
		 * image format which choose what happen.
		 */

		/**
		 *  Get comment string #1.
		 *  @note   This method should return at most 80 ascii bytes, otherwise it may become truncated by some file formats.
		 *          non-printable characters, line feeds and tabs are not allowed.
		 */
		static string getComment1()
		{
#ifdef METADATA_PLATFORM_STRING
			// METADATA_PLATFORM_STRING should be in a form similar to 'i686-pc-linux-gnu', 'i386-pc-win', etc.
			return string("Platform: ") + METADATA_PLATFORM_STRING;
#else
			return string();
#endif
		}

		/**
		 *  Get comment string #2.
		 *  @note   This method should return at most 80 ascii bytes, otherwise it may become truncated by some file formats.
		 *          non-printable characters, line feeds and tabs are not allowed.
		 */
		static string getComment2()
		{
#ifdef METADATA_COMPILER_STRING
			// METADATA_COMPILER_STRING should be in a form similar to 'g++ 4.4.3', 'msvc 10.0', etc.
			return string("Compiler: ") + METADATA_COMPILER_STRING;
#else
			return string();
#endif
		}

		/**
		 *  Get comment string #3.
		 *  @note   This method should return at most 80 ascii bytes, otherwise it may become truncated by some file formats.
		 *          non-printable characters, line feeds and tabs are not allowed.
		 */
		static string getComment3()
		{
#ifdef METADATA_COMMENT_3
			// NB it is legal for METADATA_COMMENT_3 to be a function returning string
			// Note that it may be called more than once
			return string(METADATA_COMMENT_3);
#else
			return string();
#endif
		}

		/**
		 *  Get comment string #4.
		 *  @note   This method should return at most 80 ascii bytes, otherwise it may become truncated by some file formats.
		 *          non-printable characters, line feeds and tabs are not allowed.
		 */
		static string getComment4()
		{
#ifdef METADATA_COMMENT_4
			// NB it is legal for METADATA_COMMENT_4 to be a function returning string
			// Note that it may be called more than once
			return string(METADATA_COMMENT_4);
#else
			return string();
#endif
		}

		/// Get date string in ISO 8601 format.
		string getDateTime() const
		{
			char buf[50];

			std::strftime(buf, 50, "%F %TZ", &date);

			return buf;
		}

		/// Get year (including full century)
		int getYear() const { return date.tm_year; }
		/// Get month (1..12)
		int getMonth() const { return date.tm_mon; }
		/// Get day of month (1..31)
		int getDay() const { return date.tm_mday; }
		/// Get hour of day (0..23)
		int getHour() const { return date.tm_hour; }
		/// Get minute of hour (0..59)
		int getMin() const { return date.tm_min; }
		/// Get second of minute (0..59)
		int getSec() const { return date.tm_sec; }

	protected:
		std::tm date;
};

}
#endif
